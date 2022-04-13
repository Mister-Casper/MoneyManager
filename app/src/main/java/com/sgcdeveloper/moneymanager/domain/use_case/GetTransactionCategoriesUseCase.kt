package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import com.sgcdeveloper.moneymanager.data.db.TransactionCategoriesDatabase
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionCategoryEntry
import com.sgcdeveloper.moneymanager.domain.model.AllExpense
import com.sgcdeveloper.moneymanager.domain.model.None
import com.sgcdeveloper.moneymanager.domain.model.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.model.Transfers
import com.sgcdeveloper.moneymanager.presentation.theme.wallets_map
import com.sgcdeveloper.moneymanager.util.isDouble
import javax.inject.Inject

class GetTransactionCategoriesUseCase @Inject constructor(
    private val context: Context,
    private val moneyManagerRepository: TransactionCategoriesDatabase
) {

    suspend fun getConverterAllItems(): List<TransactionCategory> {
        return listOf(None(context), Transfers(context)) + getCategoryTransactions()
    }

    fun getConverterAllItems(entries: List<TransactionCategoryEntry>): List<TransactionCategory> {
        return mapEntries(entries) + Transfers(context)
    }

    suspend fun getAllExpenseItems(): List<TransactionCategory> {
        return listOf(AllExpense(context)) + getCategoryTransactions().filter { it.isExpense }
    }

    suspend fun getExpenseItems(): List<TransactionCategory> {
        return getCategoryTransactions().filter { it.isExpense }.sortedBy { it.entry.order }
    }

    suspend fun getIncomeItems(): List<TransactionCategory> {
        return getCategoryTransactions().filter { !it.isExpense }.sortedBy { it.entry.order }
    }

    private suspend fun getCategoryTransactions(): List<TransactionCategory> {
        return mapEntries(moneyManagerRepository.transactionCategoryDao().getTransactionCategories())
    }

    private fun mapEntries(entries: List<TransactionCategoryEntry>): List<TransactionCategory> {
        return entries.map {
            val icon = context.resources.getIdentifier(it.icon, "drawable", context.packageName)
            val color = if (it.color.isDouble()) it.color.toInt() else wallets_map[it.color]!!.toArgb()
            val description = if (it.isDefault == 1)
                context.getString(context.resources.getIdentifier(it.description, "string", context.packageName))
            else it.description

            TransactionCategory(it.id, icon, color, description, it.isDefault == 1, it.isExpense == 1, it, it.order)
        }
    }
}