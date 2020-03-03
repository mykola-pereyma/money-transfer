package com.money.transfer.web

import java.math.BigDecimal

class TransferRequestValidator: TransferValidator {
    override fun isValidTransferRequest(
        senderAccount: Long,
        receiverAccount: Long,
        amount: BigDecimal
    ): ValidationResult =
        if (senderAccount < 1111111111111111 || receiverAccount > 9999999999999999)
            ValidationResult(false, "Sender account is not valid")
        else if (receiverAccount < 1111111111111111 || receiverAccount > 9999999999999999)
            ValidationResult(false, "Receiver account is not valid")
        else if (amount.toDouble() <= 0.0)
            ValidationResult(
                false,
                "Amount must be positive and greater then 0"
            )
        else
            ValidationResult(true, "Transfer request is valid")
}