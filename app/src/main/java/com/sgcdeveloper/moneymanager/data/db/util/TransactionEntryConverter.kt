package com.sgcdeveloper.moneymanager.data.db.util

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.sgcdeveloper.moneymanager.data.db.TransactionCategoriesDatabase
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionCategoriesUseCase
import com.sgcdeveloper.moneymanager.util.gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@ProvidedTypeConverter
class TransactionEntryConverter @Inject constructor(
    private val getTransactionCategoriesUseCase: GetTransactionCategoriesUseCase,
    transactionCategoriesDatabase: TransactionCategoriesDatabase
) {

    var categories = runBlocking { getTransactionCategoriesUseCase.getAllItems().associateBy { it.id } }

    init {
        transactionCategoriesDatabase.transactionCategoryDao().getTransactionCategoriesLive().observeForever {
            GlobalScope.launch {
                categories = getTransactionCategoriesUseCase.getAllItems().associateBy { it.id }
            }
        }
    }

    @TypeConverter
    fun fromDate(transactionEntry: TransactionEntry): String {
        return gson.toJson(transactionEntry)
    }

    @TypeConverter
    fun toDate(transactionEntry: String): TransactionEntry {
        val entry = gson.fromJson(transactionEntry, TransactionEntry::class.java)
        val category = try {
            categories[entry.category.id]!!
        } catch (ex: Exception) {
            runBlocking {
                categories = getTransactionCategoriesUseCase.getAllItems().associateBy { it.id }
                categories[entry.category.id]!!
            }
        }
        return entry.copy(category = category)
    }

}