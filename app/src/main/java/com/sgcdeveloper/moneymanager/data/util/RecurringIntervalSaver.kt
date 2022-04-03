package com.sgcdeveloper.moneymanager.data.util

import com.sgcdeveloper.moneymanager.domain.model.Recurring
import com.sgcdeveloper.moneymanager.domain.model.Recurring.*
import com.sgcdeveloper.moneymanager.domain.model.RecurringInterval
import com.sgcdeveloper.moneymanager.util.gson

class RecurringIntervalSaver(val type: Recurring, val recurringInterval: String) {

    fun toRecurringInterval(): RecurringInterval {
        return when (type) {
            None -> throw Exception()
            Daily -> gson.fromJson(recurringInterval, RecurringInterval.Daily::class.java)
            Weekly -> gson.fromJson(recurringInterval, RecurringInterval.Weekly::class.java)
            Monthly -> gson.fromJson(recurringInterval, RecurringInterval.Monthly::class.java)
            Yearly -> gson.fromJson(recurringInterval, RecurringInterval.Yearly::class.java)
        }
    }
}