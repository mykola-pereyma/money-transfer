package com.money.transfer.web

import com.money.transfer.model.NewTransferRequest
import com.money.transfer.service.MoneyTransferService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

fun Route.transactions(moneyTransferService: MoneyTransferService) {

    val validator = TransferRequestValidator()

    route("/account-transactions") {
        get("/") {
            call.respond(moneyTransferService.getAllTransactions())
        }
    }

    route("/transfer-requests"){
        post("/"){
            val transferRequest = call.receive<NewTransferRequest>()

            val validationResult = validator.isValidTransferRequest(
                transferRequest.senderAccount,
                transferRequest.receiverAccount,
                transferRequest.amount)

            if (validationResult.valid) call.respond(moneyTransferService.handleTransferRequest(transferRequest))
            else call.respond(HttpStatusCode.BadRequest, validationResult)
        }
        get("/") {
            call.respond(moneyTransferService.getAllTransferRequests())
        }

    }
}