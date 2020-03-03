package com.money.transfer.web

import com.money.transfer.model.CurrencyCode
import com.money.transfer.model.NewTransferRequest
import com.money.transfer.model.TransferRequestStatus
import com.money.transfer.model.TransferResponse
import com.money.transfer.server.ServerTest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class MoneyTransferResourceTest : ServerTest() {

    @Test
    fun testTransferRequestEnoughFunds() {
        //given initial transfer
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(NewTransferRequest(
                null,
                9999999999999999,
                1111222233334444,
                CurrencyCode.EUR, BigDecimal(100),
                null))
            .When()
            .post("/transfer-requests")
            .then()
            .statusCode(200)

        // when
        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(NewTransferRequest(
                null,
                1111222233334444,
                5555666677778888,
                CurrencyCode.EUR, BigDecimal(100),
                null))
            .When()
            .post("/transfer-requests")
            .then()
            .statusCode(200)
            .extract()
            .to<TransferResponse>()

        // then
        Assertions.assertThat(response.status).isEqualTo(TransferRequestStatus.PROCESSED)
    }

    @Test
    fun testTransferRequestNotEnoughFunds() {
        //given initial transfer
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(NewTransferRequest(
                null,
                9999999999999999,
                1111222233334444,
                CurrencyCode.EUR, BigDecimal(100),
                null))
            .When()
            .post("/transfer-requests")
            .then()
            .statusCode(200)

        // when
        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(NewTransferRequest(
                null,
                1111222233334444,
                5555666677778888,
                CurrencyCode.EUR, BigDecimal(200),
                null))
            .When()
            .post("/transfer-requests")
            .then()
            .statusCode(200)
            .extract()
            .to<TransferResponse>()

        // then
        Assertions.assertThat(response.status).isEqualTo(TransferRequestStatus.FAILED_NO_FUNDS)
    }

    @Test
    fun testTransferRequestWithWrongSenderAccount() {
        //given initial transfer
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(NewTransferRequest(
                null,
                9999999999999999,
                1111222233334444,
                CurrencyCode.EUR, BigDecimal(100),
                null))
            .When()
            .post("/transfer-requests")
            .then()
            .statusCode(200)

        // when
        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(NewTransferRequest(
                null,
                111122223333444,
                5555666677778888,
                CurrencyCode.EUR, BigDecimal(200),
                null))
            .When()
            .post("/transfer-requests")
            .then()
            .statusCode(400)
            .extract()
            .to<ValidationResult>()

        // then
        Assertions.assertThat(response.valid).isEqualTo(false)
        Assertions.assertThat(response.message).isEqualTo("Sender account is not valid")
    }

    @Test
    fun testTransferRequestWithWrongReceiverAccount() {
        //given initial transfer
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(NewTransferRequest(
                null,
                9999999999999999,
                1111222233334444,
                CurrencyCode.EUR, BigDecimal(100),
                null))
            .When()
            .post("/transfer-requests")
            .then()
            .statusCode(200)

        // when
        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(NewTransferRequest(
                null,
                1111222233334444,
                555566667777888,
                CurrencyCode.EUR, BigDecimal(200),
                null))
            .When()
            .post("/transfer-requests")
            .then()
            .statusCode(400)
            .extract()
            .to<ValidationResult>()

        // then
        Assertions.assertThat(response.valid).isEqualTo(false)
        Assertions.assertThat(response.message).isEqualTo("Receiver account is not valid")
    }

    @Test
    fun testTransferRequestWithNegativeAmount() {
        //given initial transfer
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(NewTransferRequest(
                null,
                9999999999999999,
                1111222233334444,
                CurrencyCode.EUR, BigDecimal(100),
                null))
            .When()
            .post("/transfer-requests")
            .then()
            .statusCode(200)

        // when
        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(NewTransferRequest(
                null,
                1111222233334444,
                5555666677778888,
                CurrencyCode.EUR, BigDecimal(-200),
                null))
            .When()
            .post("/transfer-requests")
            .then()
            .statusCode(400)
            .extract()
            .to<ValidationResult>()

        // then
        Assertions.assertThat(response.valid).isEqualTo(false)
        Assertions.assertThat(response.message).isEqualTo("Amount must be positive and greater then 0")
    }
}