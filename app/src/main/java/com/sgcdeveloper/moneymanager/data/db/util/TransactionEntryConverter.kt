package com.sgcdeveloper.moneymanager.data.db.util

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionCategoriesUseCase
import com.sgcdeveloper.moneymanager.util.gson
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@ProvidedTypeConverter
class TransactionEntryConverter @Inject constructor(private val getTransactionCategoriesUseCase: GetTransactionCategoriesUseCase) {

    val categories = runBlocking { getTransactionCategoriesUseCase.getAllItems().associateBy { it.id } }

    @TypeConverter
    fun fromDate(transactionEntry: TransactionEntry): String {
        return gson.toJson(transactionEntry)
    }

    @TypeConverter
    fun toDate(transactionEntry: String): TransactionEntry {
        val entry = gson.fromJson(transactionEntry, TransactionEntry::class.java)
        val category = categories[entry.category.id]
            ?: throw Exception("Cant find transaction category with id = " + entry.category.id)
        return entry.copy(category = category)
    }

}