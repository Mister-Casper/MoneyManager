package com.sgcdeveloper.moneymanager.data.db.util

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.sgcdeveloper.moneymanager.data.db.TransactionCategoriesDatabase
import com.sgcdeveloper.moneymanager.domain.model.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionCategoriesUseCase
import com.sgcdeveloper.moneymanager.util.gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.reflect.Type
import javax.inject.Inject

@ProvidedTypeConverter
class ListConverter @Inject constructor(
    private val getTransactionCategoriesUseCase: GetTransactionCategoriesUseCase,
    transactionCategoriesDatabase: TransactionCategoriesDatabase
) {

    var categories = runBlocking { getTransactionCategoriesUseCase.getAllItems().associateBy { it.id.toInt() } }

    init {
        transactionCategoriesDatabase.transactionCategoryDao().getTransactionCategoriesLive().observeForever {
            GlobalScope.launch {
                categories = getTransactionCategoriesUseCase.getAllItems().associateBy { it.id.toInt() }
            }
        }
    }

    @TypeConverter
    fun fromString(value: String?): List<TransactionCategory> {
        val listType: Type = object : TypeToken<List<Int>>() {}.type
        return try {
            gson.fromJson<List<Int>>(value, listType).map { id: Int -> categories[id]!! }
        }catch (ex:Exception) {
            runBlocking {
                categories = getTransactionCategoriesUseCase.getAllItems().associateBy { it.id.toInt() }
                gson.fromJson<List<Int>>(value, listType).map { id: Int -> categories[id]!! }
            }
        }
    }

    @TypeConverter
    fun fromArrayList(list: List<TransactionCategory>): String {
        return gson.toJson(list.map { it.id })
    }
}