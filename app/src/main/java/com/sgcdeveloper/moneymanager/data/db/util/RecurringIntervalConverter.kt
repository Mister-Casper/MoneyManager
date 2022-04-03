package com.sgcdeveloper.moneymanager.data.db.util

import androidx.room.TypeConverter
import com.sgcdeveloper.moneymanager.data.util.RecurringIntervalSaver
import com.sgcdeveloper.moneymanager.domain.model.RecurringInterval
import com.sgcdeveloper.moneymanager.util.gson

class RecurringIntervalConverter {

    @TypeConverter
    fun fromDate(recurringInterval: RecurringInterval): String {
        return gson.toJson(RecurringIntervalSaver(recurringInterval.recurring, gson.toJson(recurringInterval)))
    }

    @TypeConverter
    fun toDate(recurringInterval: String): RecurringInterval {
        return gson.fromJson(recurringInterval,RecurringIntervalSaver::class.java).toRecurringInterval()
    }

}