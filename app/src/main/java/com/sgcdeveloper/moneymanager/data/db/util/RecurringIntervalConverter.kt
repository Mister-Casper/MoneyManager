package com.sgcdeveloper.moneymanager.data.db.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.sgcdeveloper.moneymanager.data.util.RecurringIntervalSaver
import com.sgcdeveloper.moneymanager.domain.model.RecurringInterval

class RecurringIntervalConverter {

    @TypeConverter
    fun fromDate(recurringInterval: RecurringInterval): String {
        return Gson().toJson(RecurringIntervalSaver(recurringInterval.recurring, Gson().toJson(recurringInterval)))
    }

    @TypeConverter
    fun toDate(recurringInterval: String): RecurringInterval {
        return Gson().fromJson(recurringInterval,RecurringIntervalSaver::class.java).toRecurringInterval()
    }

}