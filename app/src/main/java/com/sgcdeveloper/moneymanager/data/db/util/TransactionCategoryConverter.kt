package com.sgcdeveloper.moneymanager.data.db.util

import androidx.room.TypeConverter
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory

class TransactionCategoryConverter {

    @TypeConverter
    fun toStr(transactionCategory: TransactionCategory): Int {
        return transactionCategory.id
    }

    @TypeConverter
    fun toCurrency(transactionCategoryId: Int): TransactionCategory {
        return TransactionCategory.findById(transactionCategoryId)
    }

}