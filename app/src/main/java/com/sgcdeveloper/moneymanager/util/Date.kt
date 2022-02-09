package com.sgcdeveloper.moneymanager.util

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

    override fun toString(): String {
        val f =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.getDefault())
        return f.format(getZoneDateTime())
    }

    fun toDateString(): String {
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

}