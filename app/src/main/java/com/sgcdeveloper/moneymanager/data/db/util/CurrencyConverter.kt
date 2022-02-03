package com.sgcdeveloper.moneymanager.data.db.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.sgcdeveloper.moneymanager.domain.model.Currency

class CurrencyConverter {

    @TypeConverter
    fun toStr(currency: Currency): String {
       return Gson().toJson(currency)
    }

    @TypeConverter
    fun toCurrency(currency: String): Currency {
        return Gson().fromJson(currency, Currency::class.java)
    }

}