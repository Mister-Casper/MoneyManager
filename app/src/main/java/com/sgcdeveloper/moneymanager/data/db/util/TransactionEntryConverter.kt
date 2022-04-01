package com.sgcdeveloper.moneymanager.data.db.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory

class TransactionEntryConverter {

    @TypeConverter
    fun fromDate(transactionEntry: TransactionEntry): String {
        return Gson().toJson(transactionEntry)
    }

    @TypeConverter
    fun toDate(transactionEntry: String): TransactionEntry {
        val entry =  Gson().fromJson(transactionEntry,TransactionEntry::class.java)
        return entry.copy(category = TransactionCategory.findById(entry.category.id))
    }

}