package com.money.transfer.service

import com.money.transfer.model.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class MoneyTransferService {

    fun getAllTransactions(): List<AccountTransaction> = transaction {
        AccountTransactions.selectAll().map { toTransaction(it) }
    }

    suspend fun getAccountTransactions(accountId: Long): List<AccountTransaction> =
        newSuspendedTransaction(Dispatchers.IO) {
            AccountTransactions.select {
                (AccountTransactions.account eq accountId)
            }.map { toTransaction(it) }
        }

    suspend fun handleTransferRequest(transferRequest: NewTransferRequest): TransferResponse {
        var transferRequestId = 0
        var requestStatus = TransferRequestStatus.PENDING
        newSuspendedTransaction(Dispatchers.IO) {
            transferRequestId = (TransferRequests.insert {
                it[senderAccount] = transferRequest.senderAccount
                it[receiverAccount] = transferRequest.receiverAccount
                it[amount] = transferRequest.amount
                it[currency] = transferRequest.currencyCode
                it[status] = TransferRequestStatus.PENDING
                it[dateUpdated] = System.currentTimeMillis()
            } get TransferRequests.id)


            val funds = AccountTransactions.select { AccountTransactions.account eq transferRequest.senderAccount }
                .asSequence().sumByDouble { it[AccountTransactions.amount].toDouble() }
            val valid =
                transferRequest.senderAccount == 9999999999999999 // authorized account to transfer money into system
                        || funds >= transferRequest.amount.toDouble()

            if (valid) {
                AccountTransactions.insert {
                    it[account] = transferRequest.senderAccount
                    it[amount] = -transferRequest.amount
                    it[requestId] = transferRequestId
                    it[dateUpdated] = System.currentTimeMillis()
                }
                AccountTransactions.insert {
                    it[account] = transferRequest.receiverAccount
                    it[amount] = transferRequest.amount
                    it[requestId] = transferRequestId
                    it[dateUpdated] = System.currentTimeMillis()
                }
                requestStatus = TransferRequestStatus.PROCESSED

                TransferRequests.update({ TransferRequests.id eq transferRequestId }) {
                    it[status] = requestStatus
                    it[dateUpdated] = System.currentTimeMillis()
                }
            } else {
                requestStatus = TransferRequestStatus.FAILED_NO_FUNDS

                TransferRequests.update({ TransferRequests.id eq transferRequestId }) {
                    it[status] = requestStatus
                }
            }
        }

        return TransferResponse(transferRequestId, requestStatus, System.currentTimeMillis())
    }

    private fun toTransaction(row: ResultRow): AccountTransaction =
        AccountTransaction(
            id = row[AccountTransactions.id],
            account = row[AccountTransactions.account],
            amount = row[AccountTransactions.amount],
            requestId = row[AccountTransactions.requestId],
            dateUpdated = row[AccountTransactions.dateUpdated]
        )

    fun getAllTransferRequests(): List<TransferRequest> = transaction {
        TransferRequests.selectAll().map { toTransferRequest(it) }
    }

    private fun toTransferRequest(resultRow: ResultRow): TransferRequest =
        TransferRequest(
            id = resultRow[TransferRequests.id],
            senderAccount = resultRow[TransferRequests.senderAccount],
            receiverAccount = resultRow[TransferRequests.receiverAccount],
            amount = resultRow[TransferRequests.amount],
            status = resultRow[TransferRequests.status],
            currencyCode = resultRow[TransferRequests.currency],
            dateUpdated = resultRow[TransferRequests.dateUpdated]
        )

}