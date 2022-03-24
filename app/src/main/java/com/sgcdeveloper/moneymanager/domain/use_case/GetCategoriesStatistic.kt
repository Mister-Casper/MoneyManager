package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import com.github.mikephil.charting.data.PieEntry
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.BaseTransactionItem
import com.sgcdeveloper.moneymanager.domain.model.CategoryStatistic
import com.sgcdeveloper.moneymanager.domain.model.Transaction
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionItems.Companion.getFormattedMoney
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.theme.red
import com.sgcdeveloper.moneymanager.util.toRoundString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

class GetCategoriesStatistic @Inject constructor(private val context: Context) {

    suspend fun getExpenseStatistic(
        transactions: List<BaseTransactionItem.TransactionItem>,
        wallet: Wallet
    ): List<CategoryStatistic> {
        return getCategoriesStatistic(
            wallet,
            transactions
        ) { return@getCategoriesStatistic it.transactionEntry.transactionType == TransactionType.Expense || (it.transactionEntry.transactionType == TransactionType.Transfer && it.transactionEntry.fromWalletId == wallet.walletId) }
    }

    suspend fun getIncomeStatistic(
        transactions: List<BaseTransactionItem.TransactionItem>,
        wallet: Wallet
    ): List<CategoryStatistic> {
        return getCategoriesStatistic(
            wallet, transactions,
        ) { return@getCategoriesStatistic it.transactionEntry.transactionType == TransactionType.Income || (it.transactionEntry.transactionType == TransactionType.Transfer && it.transactionEntry.toWalletId == wallet.walletId) }
    }

    suspend fun getCategoriesStatistic(
        wallet: Wallet,
        transaction: List<BaseTransactionItem.TransactionItem>,
        filter: (it: BaseTransactionItem.TransactionItem) -> Boolean = {true}
    ): List<CategoryStatistic> = CoroutineScope(Dispatchers.IO).async {
        val categories = mutableListOf<CategoryStatistic>()

        var maxSum = 0.0

        transaction.filter { filter(it) }
            .groupBy { it.transactionEntry.category.icon }.values.forEach { oneCategoryTransactions ->
                val firstTransaction = oneCategoryTransactions[0]
                var sum = 0.0
                oneCategoryTransactions.forEach { transaction ->
                    sum += transaction.transactionEntry.value
                }
                val transactionsCount = if (oneCategoryTransactions.size == 1)
                    context.getString(R.string.one_transaction)
                else
                    context.getString(R.string.transactions_count, oneCategoryTransactions.size)
                categories.add(
                    CategoryStatistic(
                        sum = sum,
                        category = context.getString(firstTransaction.transactionEntry.category.description),
                        categoryEntry = firstTransaction.transactionEntry.category,
                        color = firstTransaction.transactionEntry.category.color,
                        moneyColor = firstTransaction.moneyColor,
                        icon = firstTransaction.transactionEntry.category.icon,
                        money = getFormattedMoney(wallet, sum),
                        count = transactionsCount
                    )
                )
                maxSum += sum
            }

        categories.forEach {
            it.pieEntry = PieEntry((it.sum / maxSum * 100).toInt().toFloat(), it.category + " " + it.money)
            it.percent = (it.sum / maxSum * 100).toRoundString() + " %"
        }

        return@async categories.sortedByDescending { it.sum }
    }.await()

    suspend fun getExpenseCategoriesStatistic(
        transaction: List<Transaction>,
        wallet: Wallet,
        timeIntervalController: TimeIntervalController,
        filterCategories:List<TransactionCategory.ExpenseCategory>
    ): List<CategoryStatistic> = CoroutineScope(Dispatchers.IO).async {
        val filterCategoriesId = filterCategories.map { it.id }
        val categories = mutableListOf<CategoryStatistic>()

        var maxSum = 0.0

        transaction.filter { timeIntervalController.isInInterval(it.date) && filterCategoriesId.contains(it.category.id)}
            .groupBy { it.category.icon }.values.forEach { oneCategoryTransactions ->
                val firstTransaction = oneCategoryTransactions[0]
                var sum = 0.0
                oneCategoryTransactions.forEach { transaction ->
                    sum += transaction.value
                }
                val transactionsCount = if (oneCategoryTransactions.size == 1)
                    context.getString(R.string.one_transaction)
                else
                    context.getString(R.string.transactions_count, oneCategoryTransactions.size)
                categories.add(
                    CategoryStatistic(
                        sum = sum,
                        category = context.getString(firstTransaction.category.description),
                        categoryEntry = firstTransaction.category,
                        color = firstTransaction.category.color,
                        moneyColor = red.toArgb(),
                        icon = firstTransaction.category.icon,
                        money = getFormattedMoney(wallet, sum),
                        count = transactionsCount
                    )
                )
                maxSum += sum
            }

        categories.forEach {
            it.pieEntry = PieEntry((it.sum / maxSum * 100).toInt().toFloat(), it.category + " " + it.money)
            it.percent = (it.sum / maxSum * 100).toRoundString()
        }

        return@async categories.sortedByDescending { it.sum }
    }.await()
}