package com.money.transfer.service

import com.money.transfer.model.CurrencyCode
import com.money.transfer.model.NewTransferRequest
import com.money.transfer.model.TransferRequestStatus
import com.money.transfer.server.ServerTest
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class MoneyTransferServiceTest : ServerTest() {

    private val transferService = MoneyTransferService()

    @Test
    fun testTransferEnoughFunds() = runBlocking {
        // given
        transferService.handleTransferRequest(
            NewTransferRequest(
                null,
                9999999999999999,
                1111222233334444,
                CurrencyCode.EUR, BigDecimal(100), null
            )
        )
        val senderSum =
            transferService.getAccountTransactions(1111222233334444).sumByDouble { t -> t.amount.toDouble() }

        // when
        val request = NewTransferRequest(
            null,
            1111222233334444,
            5555666677778888,
            CurrencyCode.EUR, BigDecimal(100), null
        )
        val transferResponse = transferService.handleTransferRequest(request)

        // then
        assertThat(senderSum).isEqualTo(100.0)

        assertThat(transferResponse.status).isEqualTo(TransferRequestStatus.PROCESSED)

        val currentSenderSum =
            transferService.getAccountTransactions(request.senderAccount).sumByDouble { t -> t.amount.toDouble() }
        val receiverSum =
            transferService.getAccountTransactions(request.receiverAccount).sumByDouble { t -> t.amount.toDouble() }
        assertThat(currentSenderSum).isEqualTo(0.0)
        assertThat(receiverSum).isEqualTo(100.0)
        Unit
    }

    @Test
    fun testTransferNoFunds() = runBlocking {
        // given

        // when
        val request = NewTransferRequest(
            null,
            1111222233334444,
            5555666677778888,
            CurrencyCode.EUR, BigDecimal(100), null
        )
        val transferResponse = transferService.handleTransferRequest(request)

        // then
        assertThat(transferResponse.status).isEqualTo(TransferRequestStatus.FAILED_NO_FUNDS)

        val senderSum =
            transferService.getAccountTransactions(request.senderAccount).sumByDouble { t -> t.amount.toDouble() }
        val receiverSum =
            transferService.getAccountTransactions(request.receiverAccount).sumByDouble { t -> t.amount.toDouble() }
        assertThat(senderSum).isEqualTo(0.0)
        assertThat(receiverSum).isEqualTo(0.0)
        Unit
    }

}