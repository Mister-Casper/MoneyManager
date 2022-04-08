package com.sgcdeveloper.moneymanager.domain.util

import com.sgcdeveloper.moneymanager.domain.model.Recurring
import com.sgcdeveloper.moneymanager.domain.model.RecurringEndType
import com.sgcdeveloper.moneymanager.domain.model.RecurringInterval
import com.sgcdeveloper.moneymanager.util.Date
import java.time.DayOfWeek

class CreateRecurringInterval {

    operator fun invoke(
        selectedRecurring: Recurring,
        selectedRecurringType: RecurringEndType,
        until: Date,
        date: Date,
        times: Long,
        repeatInterval: Long,
        selectedDay: List<DayOfWeek>,
        isSameDay: Int
    ): RecurringInterval {
        return getRecurringInterval(
            selectedRecurring,
            selectedRecurringType,
            until,
            date,
            times,
            repeatInterval,
            selectedDay,
            isSameDay
        )
    }

    fun updateDate(recurringInterval: RecurringInterval, newDate: Date): RecurringInterval {
        return getRecurringInterval(
            recurringInterval.recurring,
            recurringInterval.type,
            recurringInterval.endDate,
            newDate,
            recurringInterval.times.toLong(),
            recurringInterval.repeatInterval.toLong(),
            recurringInterval.days,
            recurringInterval.sameDay
        )
    }

    private fun getEndDate(
        selectedRecurring: Recurring,
        selectedRecurringType: RecurringEndType,
        until: Date,
        date: Date,
        times: Long,
        repeatInterval: Long,
        selectedDay: List<DayOfWeek>,
        isSameDay: Int,
    ): Date {
        return when (selectedRecurring) {
            Recurring.None -> throw Exception()
            Recurring.Daily -> {
                if (selectedRecurringType == RecurringEndType.Until || selectedRecurringType == RecurringEndType.Forever) {
                    until
                } else {
                    Date(date.getAsLocalDate().plusDays((times.toLong()) * repeatInterval.toInt() - 1))
                }
            }
            Recurring.Weekly -> {
                if (selectedRecurringType == RecurringEndType.Until || selectedRecurringType == RecurringEndType.Forever) {
                    until
                } else {
                    var happened = 0
                    var i = 0L
                    var endDate = date.getAsLocalDate()
                    while (happened != times.toInt()) {
                        if (selectedDay.contains(endDate.dayOfWeek))
                            happened++
                        endDate = endDate.plusDays(1)
                        i++
                    }
                    Date(endDate)
                }
            }
            Recurring.Monthly -> {
                if (selectedRecurringType == RecurringEndType.Until || selectedRecurringType == RecurringEndType.Forever) {
                    until
                } else {
                    if (isSameDay != -1) {
                        val endDate = date.getAsLocalDate().plusMonths(times.toLong())
                        endDate.withDayOfMonth(endDate.lengthOfMonth())
                    }
                    Date(date.getAsLocalDate().plusMonths(times.toLong()))
                }
            }
            Recurring.Yearly -> {
                if (selectedRecurringType == RecurringEndType.Until || selectedRecurringType == RecurringEndType.Forever) {
                    until
                } else {
                    Date(date.getAsLocalDate().plusYears(times.toLong()))
                }
            }
        }
    }

    private fun getRecurringInterval(
        selectedRecurring: Recurring,
        selectedRecurringType: RecurringEndType,
        until: Date,
        date: Date,
        times: Long,
        repeatInterval: Long,
        selectedDay: List<DayOfWeek>,
        isSameDay: Int,
    ): RecurringInterval {
        return when (selectedRecurring) {
            Recurring.None -> RecurringInterval.None
            Recurring.Daily -> RecurringInterval.Daily(
                null,
                selectedRecurringType == RecurringEndType.Forever,
                getEndDate(
                    selectedRecurring,
                    selectedRecurringType,
                    until,
                    date,
                    times,
                    repeatInterval,
                    selectedDay,
                    isSameDay
                ),
                repeatInterval.toInt(),
                times.toInt(),
                selectedRecurringType
            )
            Recurring.Weekly -> RecurringInterval.Weekly(
                selectedDay,
                null,
                selectedRecurringType == RecurringEndType.Forever,
                getEndDate(
                    selectedRecurring,
                    selectedRecurringType,
                    until,
                    date,
                    times,
                    repeatInterval,
                    selectedDay,
                    isSameDay
                ),
                repeatInterval.toInt(),
                times.toInt(),
                selectedRecurringType
            )
            Recurring.Monthly -> RecurringInterval.Monthly(
                isSameDay,
                null,
                selectedRecurringType == RecurringEndType.Forever,
                getEndDate(
                    selectedRecurring,
                    selectedRecurringType,
                    until,
                    date,
                    times,
                    repeatInterval,
                    selectedDay,
                    isSameDay
                ),
                repeatInterval.toInt(),
                times.toInt(),
                selectedRecurringType
            )
            Recurring.Yearly -> RecurringInterval.Yearly(
                null,
                selectedRecurringType == RecurringEndType.Forever,
                getEndDate(
                    selectedRecurring,
                    selectedRecurringType,
                    until,
                    date,
                    times,
                    repeatInterval,
                    selectedDay,
                    isSameDay
                ),
                repeatInterval.toInt(),
                times.toInt(),
                selectedRecurringType
            )
        }
    }

}