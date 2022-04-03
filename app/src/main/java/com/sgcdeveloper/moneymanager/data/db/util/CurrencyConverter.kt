package com.sgcdeveloper.moneymanager.data.db.util

import androidx.room.TypeConverter
import com.sgcdeveloper.moneymanager.domain.model.Currency
import com.sgcdeveloper.moneymanager.util.gson

class CurrencyConverter {
    @TypeConverter
    fun toStr(currency: Currency): String {
       return gson.toJson(currency)
    }

    @TypeConverter
    fun toCurrency(currency: String): Currency {
        return gson.fromJson(currency, Currency::class.java)
    }

}