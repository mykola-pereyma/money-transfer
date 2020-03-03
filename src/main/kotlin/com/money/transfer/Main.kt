package com.money.transfer

import com.fasterxml.jackson.databind.SerializationFeature
import com.money.transfer.service.DatabaseFactory
import com.money.transfer.service.MoneyTransferService
import com.money.transfer.web.transactions
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.lang.IllegalArgumentException

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)

    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
        }
    }

    install(StatusPages) {
        exception<IllegalArgumentException> { cause ->
            call.respondText(cause.localizedMessage, ContentType.Application.Json , HttpStatusCode.BadRequest)
        }
    }

    DatabaseFactory.init()

    val transactionService = MoneyTransferService()

    install(Routing) {
        transactions(transactionService)
    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args))
        .start(wait = true)
}