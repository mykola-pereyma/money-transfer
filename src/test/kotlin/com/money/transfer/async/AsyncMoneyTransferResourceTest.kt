package com.money.transfer.async

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.money.transfer.model.CurrencyCode
import com.money.transfer.model.NewTransferRequest
import com.money.transfer.model.TransferRequestStatus
import com.money.transfer.model.TransferResponse
import com.money.transfer.server.ServerTest
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.post
import io.ktor.http.content.TextContent
import io.restassured.RestAssured
import io.restassured.http.ContentType
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class AsyncMoneyTransferResourceTest : ServerTest() {

    @Test
    fun testAsyncTransferRequestsForLimitedFunds() = runBlocking {

        val accountFunds = BigDecimal(1000)

        //given initial transfer
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(
                NewTransferRequest(
                    null,
                    9999999999999999,
                    1111222233334444,
                    CurrencyCode.EUR, accountFunds,
                    null
                )
            )
            .When()
            .post("/transfer-requests")
            .then()
            .statusCode(200)

        //when
        var processed = 0;
        val a: Iterable<Deferred<TransferResponse>> = runAsyncRequests()
        a.forEach {
            if (it.await().status.equals(TransferRequestStatus.PROCESSED)) processed++
        }

        // then
        Assertions.assertThat(processed).isEqualTo(20)
        Unit
    }

    private suspend fun runAsyncRequests(): Iterable<Deferred<TransferResponse>> = coroutineScope {

        val client = HttpClient(Apache) {
            install(JsonFeature) {
                serializer = GsonSerializer {
                    // .GsonBuilder
                    serializeNulls()
                    disableHtmlEscaping()
                }
            }
        }

        (1..100).map { x ->
            async {
                client.post<TransferResponse>(
                    port = 8080, path = "/transfer-requests",
                    body = TextContent(
                        jacksonObjectMapper().writeValueAsString(
                            NewTransferRequest(
                                null,
                                1111222233334444,
                                5555666677778880 + x,
                                CurrencyCode.EUR, BigDecimal(50),
                                null
                            )
                        ), contentType = io.ktor.http.ContentType.Application.Json
                    )
                )
            }
        }.asIterable()
    }
}