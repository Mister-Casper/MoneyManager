package com.sgcdeveloper.moneymanager.data.db.util

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import java.lang.reflect.Type

class ListConverter {
    @TypeConverter
    fun  fromString(value: String?): List<TransactionCategory.ExpenseCategory> {
        val listType: Type = object : TypeToken<List<TransactionCategory.ExpenseCategory>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: List<TransactionCategory.ExpenseCategory>): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
}