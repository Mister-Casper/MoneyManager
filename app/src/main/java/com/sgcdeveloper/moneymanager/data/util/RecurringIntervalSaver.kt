package com.sgcdeveloper.moneymanager.data.util

import com.google.gson.Gson
import com.sgcdeveloper.moneymanager.domain.model.Recurring
import com.sgcdeveloper.moneymanager.domain.model.Recurring.*
import com.sgcdeveloper.moneymanager.domain.model.RecurringInterval

class RecurringIntervalSaver(val type: Recurring, val recurringInterval: String) {

    fun toRecurringInterval(): RecurringInterval {
        return when (type) {
            None -> throw Exception()
            Daily -> Gson().fromJson(recurringInterval, RecurringInterval.Daily::class.java)
            Weekly -> Gson().fromJson(recurringInterval, RecurringInterval.Weekly::class.java)
            Monthly -> Gson().fromJson(recurringInterval, RecurringInterval.Monthly::class.java)
            Yearly -> Gson().fromJson(recurringInterval, RecurringInterval.Yearly::class.java)
        }
    }
}