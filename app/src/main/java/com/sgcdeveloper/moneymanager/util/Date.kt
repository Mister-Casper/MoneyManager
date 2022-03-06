package com.sgcdeveloper.moneymanager.util

import android.annotation.SuppressLint
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

data class Date(val epochMillis: Long) {

    constructor(time: LocalDateTime) : this(time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
    constructor(time: LocalDate) : this(time.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())

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
        val f = DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(Locale.getDefault())
        return f.format(getZoneDateTime())
    }

    fun toDayMonthString(): String {
        val f = DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(Locale.getDefault())
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
        return getAsLocalDate().dayOfWeek.name
    }

    fun getMonth():String{
        return getAsLocalDate().month.name + " " + getAsLocalDate().year.toString()
    }

    fun getMonthName():String{
        return getAsLocalDate().month.name
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

    companion object{
        @SuppressLint("ConstantLocale")
        private val dateStringFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(Locale.getDefault())
    }
}