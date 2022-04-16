package com.sgcdeveloper.moneymanager.domain.timeInterval

import android.content.Context
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.util.Date
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.floor

sealed class TimeIntervalController(val icon: Int, val name: Int) {
    abstract fun moveBack()
    abstract fun moveNext()
    abstract fun isCanMove(): Boolean
    abstract fun getDescription(): String
    abstract fun getGraphTimeInterval(): Long
    abstract fun getStartDate(): Date
    abstract fun getEndDate(): Date
    abstract fun getDividersCount(): Int

    fun isInInterval(intervalDate: Date): Boolean {
        if (this is AllController)
            return true
        val date =
            ((intervalDate.epochMillis - 1) / TimeUnit.DAYS.toMillis(1)).toInt()
        val startDate =
            floor( (getStartDate().epochMillis  / TimeUnit.DAYS.toMillis(1).toDouble())).toInt()
        val endDate =
            ceil( (getEndDate().epochMillis  / TimeUnit.DAYS.toMillis(1).toDouble())).toInt()
        return (date in startDate .. endDate)
    }

    class DailyController(var date: Date = Date(LocalDate.now())) :
        TimeIntervalController(R.drawable.daily_icon, R.string.daily) {

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

        override fun getGraphTimeInterval(): Long {
            return TimeUnit.DAYS.toMillis(1)
        }

        override fun getStartDate(): Date = date
        override fun getEndDate(): Date = date
        override fun getDividersCount(): Int = 1
    }

    class WeeklyController(
        var startDay: Date = Date(LocalDate.now()),
        var endDay: Date = Date(startDay.getAsLocalDate().plusDays(6))
    ) :
        TimeIntervalController(R.drawable.weekly_icon, R.string.weekly) {

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

        override fun getGraphTimeInterval(): Long {
            return TimeUnit.DAYS.toMillis(1)
        }

        override fun getStartDate(): Date = startDay
        override fun getEndDate(): Date = endDay
        override fun getDividersCount(): Int = 7
    }

    class MonthlyController(var date: Date = Date(LocalDate.now().withDayOfMonth(1))) :
        TimeIntervalController(R.drawable.monthly_icon, R.string.monthly) {

        private var endDate = Date(date.getAsLocalDate().withDayOfMonth(date.getAsLocalDate().lengthOfMonth()))

        constructor(date: LocalDate) : this(Date(date.withDayOfMonth(1)))

        override fun moveBack() {
            date = Date(date.getAsLocalDate().minusMonths(1))
            endDate = Date(endDate.getAsLocalDate().minusMonths(1))
        }

        override fun moveNext() {
            date = Date(date.getAsLocalDate().plusMonths(1))
            endDate = Date(endDate.getAsLocalDate().plusMonths(1))
        }

        override fun isCanMove(): Boolean {
            return true
        }

        override fun getDescription(): String {
            return date.toMonthString()
        }

        override fun getGraphTimeInterval(): Long {
            return TimeUnit.DAYS.toMillis(date.getAsLocalDate().lengthOfMonth().toLong()) / 8
        }

        override fun getStartDate(): Date = date
        override fun getEndDate(): Date = endDate

        override fun getDividersCount(): Int = 8
    }

    class QuarterlyController(
        var startDay: Date = Date(LocalDate.now().withDayOfMonth(1)),
        var endDay: Date = startDay
    ) :
        TimeIntervalController(R.drawable.quarterly_icon, R.string.quarterly) {

        init {
            val start = startDay.getAsLocalDate()
            val month = start.monthValue - ((start.monthValue ) % 3)
            startDay = Date(start.withMonth(month))
            endDay = Date(endDay.getAsLocalDate().withDayOfMonth(endDay.getAsLocalDate().lengthOfMonth()).withMonth((month + 2) % 12))
        }

        constructor(startDay: LocalDate) : this(
            Date(startDay.withDayOfMonth(1)),
            Date(startDay.plusMonths(2))
        )

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
            return startDay.toMonthString() + " - " + endDay.toMonthString()
        }

        override fun getGraphTimeInterval(): Long {
            return (endDay.epochMillis - startDay.epochMillis) / 8
        }

        override fun getStartDate(): Date = startDay
        override fun getEndDate(): Date = endDay
        override fun getDividersCount(): Int = 8
    }

    class YearlyController(var date: Date = Date(LocalDate.now())) :
        TimeIntervalController(R.drawable.yearly_icon, R.string.yearly) {

        private var endDate =
            Date(date.getAsLocalDate().withMonth(12).withDayOfMonth(date.getAsLocalDate().lengthOfMonth()))

        constructor(startDay: LocalDate) : this(
            Date(startDay.withDayOfMonth(1).withMonth(1)),
        )

        override fun moveBack() {
            date = Date(date.getAsLocalDate().minusYears(1))
            endDate = Date(endDate.getAsLocalDate().minusYears(1))
        }

        override fun moveNext() {
            date = Date(date.getAsLocalDate().plusYears(1))
            endDate = Date(endDate.getAsLocalDate().plusYears(1))
        }

        override fun isCanMove(): Boolean {
            return true
        }

        override fun getDescription(): String {
            return date.getAsLocalDate().year.toString()
        }

        override fun getGraphTimeInterval(): Long {
            return TimeUnit.DAYS.toMillis(date.getAsLocalDate().lengthOfYear().toLong()) / 8
        }

        override fun getStartDate(): Date = date
        override fun getEndDate(): Date = endDate

        override fun getDividersCount(): Int = 8
    }

    class AllController(val allString: String) : TimeIntervalController(R.drawable.infinity_icon, R.string.all) {
        override fun moveBack() {}
        override fun moveNext() {}

        override fun isCanMove(): Boolean {
            return false
        }

        override fun getDescription(): String {
            return allString
        }

        override fun getGraphTimeInterval(): Long {
            return TimeUnit.DAYS.toMillis(7)
        }

        override fun getStartDate(): Date = throw java.lang.Exception("Unsupported time interval controller")
        override fun getEndDate(): Date = throw java.lang.Exception("Unsupported time interval controller")
        override fun getDividersCount(): Int = throw java.lang.Exception("Unsupported time interval controller")
    }

    companion object {
        fun getItems(context: Context): List<TimeIntervalController> {
            return listOf(
                DailyController(),
                WeeklyController(),
                MonthlyController(),
                QuarterlyController(),
                YearlyController(),
                AllController(context.getString(R.string.all)),
                CustomController()
            )
        }
    }

    class CustomController(private val step: Long? = null) :
        TimeIntervalController(R.drawable.edit_calendar_icon, R.string.custom) {
        var startIntervalDate: Date = Date(LocalDate.now())
        var endIntervalDate: Date = Date(LocalDate.now())

        override fun moveBack() {
            step?.let {
                startIntervalDate = Date(startIntervalDate.epochMillis - step)
                endIntervalDate = Date(startIntervalDate.epochMillis - step)
            }
        }

        override fun moveNext() {
            step?.let {
                startIntervalDate = Date(startIntervalDate.epochMillis + step)
                endIntervalDate = Date(startIntervalDate.epochMillis + step)
            }
        }

        override fun isCanMove(): Boolean {
            return false
        }

        override fun getDescription(): String {
            return startIntervalDate.toWeekString() + " - " + endIntervalDate.toWeekString()
        }

        override fun getGraphTimeInterval(): Long {
            return if (endIntervalDate.epochMillis - startIntervalDate.epochMillis >= TimeUnit.DAYS.toMillis(30))
                (endIntervalDate.epochMillis - startIntervalDate.epochMillis) / 8
            else
                TimeUnit.DAYS.toMillis(1)
        }

        override fun getStartDate(): Date = startIntervalDate
        override fun getEndDate(): Date = endIntervalDate
        override fun getDividersCount(): Int {
            return if (endIntervalDate.epochMillis - startIntervalDate.epochMillis >= TimeUnit.DAYS.toMillis(30))
                8
            else
                ((endIntervalDate.epochMillis - startIntervalDate.epochMillis) / TimeUnit.DAYS.toMillis(1)).toInt()
        }
    }
}