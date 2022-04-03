package com.sgcdeveloper.moneymanager.data.db.util

import androidx.room.TypeConverter
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import com.sgcdeveloper.moneymanager.util.gson

class TransactionEntryConverter {

    @TypeConverter
    fun fromDate(transactionEntry: TransactionEntry): String {
        return gson.toJson(transactionEntry)
    }

    @TypeConverter
    fun toDate(transactionEntry: String): TransactionEntry {
        val entry = gson.fromJson(transactionEntry,TransactionEntry::class.java)
        return entry.copy(category = TransactionCategory.findById(entry.category.id))
    }

}