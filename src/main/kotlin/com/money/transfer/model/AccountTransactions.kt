package com.money.transfer.model

import org.jetbrains.exposed.sql.Table
import java.math.BigDecimal

object AccountTransactions : Table() {
    val id = integer("id").autoIncrement()
    val account = long("account")
    val amount = decimal("amount", 10, 2)
    val requestId = (integer("requestId") references TransferRequests.id)
    val dateUpdated = long("dateUpdated")
    override val primaryKey = PrimaryKey(id)
}

data class AccountTransaction(
    val id: Int,
    val account: Long,
    val amount: BigDecimal,
    val requestId: Int,
    val dateUpdated: Long
)

data class NewAccountTransaction(
    val id: Int?,
    val account: Long,
    val amount: BigDecimal,
    val requestId: Int,
    val dateCreated: Long
)