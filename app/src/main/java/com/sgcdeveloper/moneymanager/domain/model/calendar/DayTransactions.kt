package com.sgcdeveloper.moneymanager.domain.model.calendar

import com.sgcdeveloper.moneymanager.domain.model.BaseTransactionItem
import java.util.*

data class DayTransactions(
    val dayText: String = "",
    val total: String = "",
    val transactions: List<BaseTransactionItem.TransactionItem> = Collections.emptyList()
)