package com.sgcdeveloper.moneymanager.domain.model.calendar

import java.util.*

data class TransactionsCalendar(
    val income: String = "",
    val expense: String = "",
    val total: String = "",
    val daysOfWeek: List<DayOfWeek> = Collections.emptyList(),
    val days: List<CalendarDay> = Collections.emptyList()
)

data class DayOfWeek(val name:String,val isHoliday:Boolean)