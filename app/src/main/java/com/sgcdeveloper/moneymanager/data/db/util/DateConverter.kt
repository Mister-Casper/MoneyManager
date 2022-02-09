package com.sgcdeveloper.moneymanager.data.db.util

import androidx.room.TypeConverter
import com.sgcdeveloper.moneymanager.util.Date

class DateConverter {

    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.epochMillis
    }

    @TypeConverter
    fun toDate(time: Long): Date {
        return Date(time)
    }

}