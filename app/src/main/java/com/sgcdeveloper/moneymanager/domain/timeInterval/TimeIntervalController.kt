package com.sgcdeveloper.moneymanager.domain.timeInterval

import android.content.Context
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.util.Date
import java.time.LocalDate

sealed class TimeIntervalController(val icon: Int, val name: Int) {
    abstract fun moveBack()
    abstract fun moveNext()
    abstract fun isCanMove(): Boolean
    abstract fun getDescription(): String
    abstract fun isInInterval(intervalDate: Date): Boolean

    class DailyController(private var date: Date = Date(LocalDate.now())) : TimeIntervalController(R.drawable.daily_icon, R.string.daily) {
        override fun moveBack() {
            date = Date(date.getAsLocalDate().minusDays(1))
        }

        override fun moveNext() {
            date = Date(date.getAsLocalDate().plusDays(1))
        }

        override fun isCanMove(): Boolean {
            return true
        }

        override fun getDescription(): String {
            return date.toDateString()
        }

        override fun isInInterval(intervalDate: Date): Boolean {
            return date.toDateString() == intervalDate.toDateString()
        }
    }

    class WeeklyController(private var startDay: Date = Date(LocalDate.now())) :
        TimeIntervalController(R.drawable.weekly_icon, R.string.weekly) {
        private var endDay: Date = Date(startDay.getAsLocalDate().plusDays(7))

        override fun moveBack() {
            startDay = Date(startDay.getAsLocalDate().minusDays(7))
            endDay = Date(endDay.getAsLocalDate().minusDays(7))
        }

        override fun moveNext() {
            startDay = Date(startDay.getAsLocalDate().plusDays(7))
            endDay = Date(endDay.getAsLocalDate().plusDays(7))
        }

        override fun isCanMove(): Boolean {
            return true
        }

        override fun getDescription(): String {
            return startDay.toWeekString() + " - " + endDay.toWeekString()
        }

        override fun isInInterval(intervalDate: Date): Boolean {
            return (startDay.toDateString() == intervalDate.toDateString()
                    || endDay.toDateString() == intervalDate.toDateString() ||
                    (intervalDate.epochMillis <= endDay.epochMillis && intervalDate.epochMillis >= startDay.epochMillis))
        }
    }

    class MonthlyController(var date: Date = Date(LocalDate.now())) : TimeIntervalController(R.drawable.monthly_icon, R.string.monthly) {
        override fun moveBack() {
            date = Date(date.getAsLocalDate().minusMonths(1))
        }

        override fun moveNext() {
            date = Date(date.getAsLocalDate().plusMonths(1))
        }

        override fun isCanMove(): Boolean {
            return true
        }

        override fun getDescription(): String {
            return date.toMonthString()
        }

        override fun isInInterval(intervalDate: Date): Boolean {
            return date.getAsLocalDate().withDayOfMonth(1) <= intervalDate.getAsLocalDate()
                    && date.getAsLocalDate()
                .withDayOfMonth(date.getAsLocalDate().lengthOfMonth()) >= intervalDate.getAsLocalDate()
                    && date.getAsLocalDate().month == intervalDate.getAsLocalDate().month
        }
    }

    class QuarterlyController(private var startDay: Date = Date(LocalDate.now())) :
        TimeIntervalController(R.drawable.quarterly_icon, R.string.quarterly) {
        private var endDay: Date = Date(startDay.getAsLocalDate().plusMonths(3))

        override fun moveBack() {
            startDay = Date(startDay.getAsLocalDate().minusMonths(3))
            endDay = Date(endDay.getAsLocalDate().minusMonths(3))
        }

        override fun moveNext() {
            startDay = Date(startDay.getAsLocalDate().plusMonths(3))
            endDay = Date(endDay.getAsLocalDate().plusMonths(3))
        }

        override fun isCanMove(): Boolean {
            return true
        }

        override fun getDescription(): String {
            return startDay.toWeekString() + " - " + endDay.toWeekString()
        }

        override fun isInInterval(intervalDate: Date): Boolean {
            return (startDay.toDateString() == intervalDate.toDateString()
                    || endDay.toDateString() == intervalDate.toDateString() ||
                    (intervalDate.epochMillis <= endDay.epochMillis && intervalDate.epochMillis >= startDay.epochMillis))
        }
    }

    class YearlyController(var date: Date = Date(LocalDate.now())) : TimeIntervalController(R.drawable.yearly_icon, R.string.yearly) {
        override fun moveBack() {
            date = Date(date.getAsLocalDate().minusYears(1))
        }

        override fun moveNext() {
            date = Date(date.getAsLocalDate().plusYears(1))
        }

        override fun isCanMove(): Boolean {
            return true
        }

        override fun getDescription(): String {
            return date.getAsLocalDate().year.toString()
        }

        override fun isInInterval(intervalDate: Date): Boolean {
            return intervalDate.getAsLocalDate().year == date.getAsLocalDate().year
        }
    }

    class AllController(private val allString: String) : TimeIntervalController(R.drawable.infinity_icon, R.string.all) {
        override fun moveBack() {}
        override fun moveNext() {}

        override fun isCanMove(): Boolean {
            return false
        }

        override fun getDescription(): String {
            return allString
        }

        override fun isInInterval(intervalDate: Date): Boolean {
            return true
        }
    }

    companion object {
        fun getItems(context: Context): List<TimeIntervalController> {
            return listOf(DailyController(), WeeklyController(), MonthlyController(), QuarterlyController(), YearlyController(), AllController(context.getString(R.string.all)))
        }
    }

}