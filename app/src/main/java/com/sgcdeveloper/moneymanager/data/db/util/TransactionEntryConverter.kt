package com.sgcdeveloper.moneymanager.data.db.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry

class TransactionEntryConverter {

    @TypeConverter
    fun fromDate(transactionEntry: TransactionEntry): String {
        return Gson().toJson(transactionEntry)
    }

    @TypeConverter
    fun toDate(transactionEntry: String): TransactionEntry {
        return Gson().fromJson(transactionEntry,TransactionEntry::class.java)
    }

}