package com.money.transfer.model

data class TransferResponse(
    val id: Int,
    val status: TransferRequestStatus,
    val dateUpdated: Long
)