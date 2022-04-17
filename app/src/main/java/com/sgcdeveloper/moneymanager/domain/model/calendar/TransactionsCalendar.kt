package com.sgcdeveloper.moneymanager.domain.model.calendar

import java.util.*

data class TransactionsCalendar(
    val income: String = "",
    val expense: String = "",
    val total: String = "",
    val daysOfWeek: List<String> = Collections.emptyList(),
    val days: List<CalendarDay> = Collections.emptyList()
)