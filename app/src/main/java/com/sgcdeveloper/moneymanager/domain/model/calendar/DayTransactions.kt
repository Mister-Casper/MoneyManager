package com.sgcdeveloper.moneymanager.domain.model.calendar

import com.sgcdeveloper.moneymanager.domain.model.BaseTransactionItem
import java.time.LocalDate
import java.util.*

data class DayTransactions(
    val dayNumber:Int = -1,
    val dayText: String = "",
    val day:LocalDate = LocalDate.now(),
    val total: String = "0",
    val transactions: List<BaseTransactionItem.TransactionItem> = Collections.emptyList()
)