package com.sgcdeveloper.moneymanager.data.db.util

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import java.lang.reflect.Type

class ListConverter {
    @TypeConverter
    fun fromString(value: String?): List<TransactionCategory.ExpenseCategory> {
        val listType: Type = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson<List<Int>>(value, listType).map { id: Int -> TransactionCategory.findById(id) as TransactionCategory.ExpenseCategory }
    }

    @TypeConverter
    fun fromArrayList(list: List<TransactionCategory.ExpenseCategory>): String {
        return Gson().toJson(list.map { it.id })
    }
}