package com.money.transfer.service

import com.money.transfer.model.AccountTransactions
import com.money.transfer.model.CurrencyCode
import com.money.transfer.model.TransferRequestStatus
import com.money.transfer.model.TransferRequests
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.sql.Connection

object DatabaseFactory {

    fun init() {
        Database.connect(hikari())
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        transaction {
            create(TransferRequests)
            create(AccountTransactions)

            val firstRequestId = TransferRequests.insert {
                it[senderAccount] = 9999999999999999
                it[receiverAccount] = 1111222233334444
                it[amount] = BigDecimal(150)
                it[currency] = CurrencyCode.EUR
                it[status] = TransferRequestStatus.PROCESSED
                it[dateUpdated] = System.currentTimeMillis()
            } get (TransferRequests.id)
            AccountTransactions.insert {
                it[account] = 1111222233334444
                it[amount] = BigDecimal(150)
                it[requestId] = firstRequestId
                it[dateUpdated] = System.currentTimeMillis()
            }

            val secondRequestId = TransferRequests.insert {
                it[senderAccount] = 9999999999999999
                it[receiverAccount] = 5555666677778888
                it[amount] = BigDecimal(250)
                it[currency] = CurrencyCode.EUR
                it[status] = TransferRequestStatus.PROCESSED
                it[dateUpdated] = System.currentTimeMillis()
            } get (TransferRequests.id)
            AccountTransactions.insert {
                it[account] = 5555666677778888
                it[amount] = BigDecimal(250)
                it[requestId] = secondRequestId
                it[dateUpdated] = System.currentTimeMillis()
            }
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.sqlite.JDBC"
        config.jdbcUrl = "jdbc:sqlite:memory:myDb"
        config.maximumPoolSize = 5
        config.isAutoCommit = false
        config.validate()
        return HikariDataSource(config)
    }
}