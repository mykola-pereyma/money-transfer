package com.money.transfer.model

import org.jetbrains.exposed.sql.Table
import java.math.BigDecimal

object TransferRequests : Table() {
    val id = integer("id").autoIncrement()
    val senderAccount = long("senderAccount")
    val receiverAccount = long("receiverAccount")
    val amount = decimal("amount", 10, 2)
    val currency = enumeration("currency", CurrencyCode::class)
    val status = enumeration("status", TransferRequestStatus::class)
    val dateUpdated = long("dateUpdated").default(System.currentTimeMillis())
    override val primaryKey = PrimaryKey(id)
}

data class TransferRequest(
    val id: Int,
    val senderAccount: Long,
    val receiverAccount: Long,
    val amount: BigDecimal,
    val currencyCode: CurrencyCode,
    val status: TransferRequestStatus,
    val dateUpdated: Long
)

data class NewTransferRequest(
    val id: Int?,
    val senderAccount: Long,
    val receiverAccount: Long,
    val currencyCode: CurrencyCode,
    val amount: BigDecimal,
    val dateUpdated: Long?
)