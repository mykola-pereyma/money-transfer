package com.money.transfer.web

import java.math.BigDecimal

data class ValidationResult(val valid: Boolean, val message: String)

interface TransferValidator {
    fun isValidTransferRequest(
        senderAccount: Long,
        receiverAccount: Long,
        amount: BigDecimal
    ): ValidationResult
}