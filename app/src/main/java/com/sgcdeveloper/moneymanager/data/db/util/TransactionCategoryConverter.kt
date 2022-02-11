package com.sgcdeveloper.moneymanager.data.db.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory

class TransactionCategoryConverter {

    @TypeConverter
    fun toStr(transactionCategory: TransactionCategory): String {
       return  GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(transactionCategory)
    }

    @TypeConverter
    fun toCurrency(transactionCategory: String): TransactionCategory {
        return Gson().fromJson(transactionCategory, TransactionCategory::class.java)
    }

}