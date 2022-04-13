package com.sgcdeveloper.moneymanager.data.db.util

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.sgcdeveloper.moneymanager.data.db.TransactionCategoriesDatabase
import com.sgcdeveloper.moneymanager.domain.model.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionCategoriesUseCase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@ProvidedTypeConverter
class TransactionCategoryConverter @Inject constructor(
    private val getTransactionCategoriesUseCase: GetTransactionCategoriesUseCase,
    transactionCategoriesDatabase: TransactionCategoriesDatabase
) {

    var categories = runBlocking { getTransactionCategoriesUseCase.getConverterAllItems().associateBy { it.id } }

    init {
        transactionCategoriesDatabase.transactionCategoryDao().getTransactionCategoriesLive().observeForever {
            GlobalScope.launch {
                categories = getTransactionCategoriesUseCase.getConverterAllItems().associateBy { it.id }
            }
        }
    }

    @TypeConverter
    fun toStr(transactionCategory: TransactionCategory): Int {
        return transactionCategory.id.toInt()
    }

    @TypeConverter
    fun toCurrency(transactionCategoryId: Int): TransactionCategory {
        return try {
            categories[transactionCategoryId.toLong()]!!
        }catch (ex:Exception){
            runBlocking {
                categories = getTransactionCategoriesUseCase.getConverterAllItems().associateBy { it.id }
                categories[transactionCategoryId.toLong()]!!
            }
        }
    }

}