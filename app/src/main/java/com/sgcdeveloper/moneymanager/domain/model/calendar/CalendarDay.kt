package com.sgcdeveloper.moneymanager.domain.model.calendar

data class CalendarDay(
    val isExist: Boolean = false,
    val number: String = "",
    val isToday: Boolean = false,
    val isHoliday: Boolean = false,
    val income: String = "",
    val expense: String = "",
    val dayTransactions: DayTransactions = DayTransactions()
)
