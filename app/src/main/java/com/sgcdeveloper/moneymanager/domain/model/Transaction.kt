package com.sgcdeveloper.moneymanager.domain.model

import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.util.Date

data class Transaction(
    val id: Long,
    val date: Date,
    val value: Double,
    val baseCurrencyValue: Double,
    val description: String,
    val transactionType: TransactionType,
    val fromWalletId: Long,
    val toWalletId: Long = 0,
    val category: TransactionCategory
)