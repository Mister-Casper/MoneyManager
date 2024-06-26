package com.sgcdeveloper.moneymanager.util

import android.annotation.SuppressLint
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.*

data class Date(val epochMillis: Long) {

    var date:String

    constructor(time: LocalDateTime) : this(time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
    constructor(time: LocalDate) : this(time.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())

    init {
        date = toDateString()
    }

    fun getAsLocalDateTime(): LocalDateTime {
        return getZoneDateTime().toLocalDateTime()
    }

    fun getAsLocalDate(): LocalDate {
        return getZoneDateTime().toLocalDate()
    }

    private fun getZoneDateTime(): ZonedDateTime {
        return Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault())
    }

    fun toShortString(): String {
        val f =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.getDefault())
        return f.format(getZoneDateTime())
    }

    override fun toString(): String {
        val f =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locale.getDefault())
        return f.format(getZoneDateTime())
    }

    fun toDateString(): String {
        return dateStringFormatter.format(getZoneDateTime())
    }

    fun toWeekString(): String {
        val f = DateTimeFormatter.ofPattern("MMM dd").withLocale(Locale.getDefault())
        return f.format(getZoneDateTime())
    }

    fun toMonthString(): String {
        val f = DateTimeFormatter.ofPattern("MMM yyyy").withLocale(Locale.getDefault())
        return f.format(getZoneDateTime())
    }

    fun toDayMonthString(): String {
        val f = DateTimeFormatter.ofPattern("dd MMM yyyy").withLocale(Locale.getDefault())
        return f.format(getZoneDateTime())
    }

    fun toShortTimeString(): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("mm:ss")
        return formatter.format(Instant.ofEpochMilli(epochMillis).atZone(ZoneId.of("UTC")))
    }

    fun toTimeString(): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return formatter.format(Instant.ofEpochMilli(epochMillis).atZone(ZoneId.of("UTC")))
    }

    fun getDay(): String {
        return getAsLocalDate().dayOfMonth.toString()
    }

    fun getDayName(): String {
        return getAsLocalDate().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).uppercase()
    }

    fun getMonth(): String {
        return getAsLocalDate().month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            .uppercase() + " " + getAsLocalDate().year.toString()
    }

    fun getMonthName(): String {
        return getAsLocalDate().month.getDisplayName(TextStyle.FULL, Locale.getDefault()).uppercase()
    }

    fun toDateString(formatStyle: FormatStyle): String {
        val f =
            DateTimeFormatter.ofLocalizedDate(formatStyle).withLocale(Locale.getDefault())
        return f.format(getZoneDateTime())
    }

    fun toLongString(): String {
        val f = DateTimeFormatter.ofPattern("yyyy-MM-dd").withLocale(Locale.getDefault())
        return f.format(getZoneDateTime())
    }

    operator fun plus(date: Date): Date {
        return Date(epochMillis + date.epochMillis)
    }

    operator fun minus(date: Date): Date {
        return Date(epochMillis - date.epochMillis)
    }

    operator fun div(date: Date): Date {
        return Date(epochMillis / date.epochMillis)
    }

    operator fun compareTo(date1:Date):Int{
        val q = Date(this.getAsLocalDateTime().with(LocalTime.MIN)).epochMillis / 1000
        val v =  Date(date1.getAsLocalDateTime().with(LocalTime.MAX)).epochMillis / 1000
        return (q - v).toInt()
    }

    companion object {
        @SuppressLint("ConstantLocale")
        private val dateStringFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy").withLocale(Locale.getDefault())

        fun LocalDate.toDateString(): String {
            return dateStringFormatter.format(this)
        }

        fun LocalDate.getDay(): String {
            return this.dayOfMonth.toString()
        }

        fun LocalDate.getDayName(): String {
            return this.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).uppercase()
        }

        fun LocalDate.getShortDayName():String{
            return this.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()
        }

        fun LocalDate.getMonthString(): String {
            return this.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                .uppercase() + " " + this.year.toString()
        }

    }
}