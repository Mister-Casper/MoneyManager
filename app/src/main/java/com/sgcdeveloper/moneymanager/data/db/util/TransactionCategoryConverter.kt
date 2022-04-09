package com.sgcdeveloper.moneymanager.data.db.util

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.sgcdeveloper.moneymanager.domain.model.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionCategoriesUseCase
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@ProvidedTypeConverter
class TransactionCategoryConverter @Inject constructor(private val getTransactionCategoriesUseCase: GetTransactionCategoriesUseCase) {

    val categories = runBlocking { getTransactionCategoriesUseCase.getAllItems().associateBy { it.id } }

    @TypeConverter
    fun toStr(transactionCategory: TransactionCategory): Int {
        return transactionCategory.id.toInt()
    }

    @TypeConverter
    fun toCurrency(transactionCategoryId: Int): TransactionCategory {
        return categories[transactionCategoryId.toLong()]!!
    }

}