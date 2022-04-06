package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import com.github.mikephil.charting.data.PieEntry
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.*
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionItems.Companion.getFormattedMoney
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.util.getMoneyColor
import com.sgcdeveloper.moneymanager.util.toRoundString
import javax.inject.Inject

class GetCategoriesStatistic @Inject constructor(private val context: Context) {

    suspend fun getExpenseStatistic(
        transactions: List<BaseTransactionItem.TransactionItem>,
        wallet: Wallet
    ): List<CategoryStatistic>  {
        return getCategoriesStatistic(
            wallet.currency,
            wallet.walletId,
            transactions.map { it.transactionEntry }
        ) { return@getCategoriesStatistic it.transactionType == TransactionType.Expense || (it.transactionType == TransactionType.Transfer && it.fromWalletId == wallet.walletId) }
    }

    suspend fun getIncomeStatistic(
        transactions: List<BaseTransactionItem.TransactionItem>,
        wallet: Wallet
    ): List<CategoryStatistic>  {
        return getCategoriesStatistic(
            wallet.currency, wallet.walletId, transactions.map { it.transactionEntry },
        ) { return@getCategoriesStatistic it.transactionType == TransactionType.Income || (it.transactionType == TransactionType.Transfer && it.toWalletId == wallet.walletId) }
    }

    suspend fun getCategoriesStatistic(
        currency: Currency,
        walletId: Long,
        transaction: List<Transaction>,
        filter: (it: Transaction) -> Boolean = { true }
    ): List<CategoryStatistic>  {
        var maxSum = 0.0

        return transaction.filter { filter(it) }
            .groupBy { it.category.icon }.values.map { oneCategoryTransactions ->
                val firstTransaction = oneCategoryTransactions[0]
                var sum = 0.0
                oneCategoryTransactions.forEach { transaction ->
                    sum += transaction.value
                }
                val transactionsCount = if (oneCategoryTransactions.size == 1)
                    context.getString(R.string.one_transaction)
                else
                    context.getString(R.string.transactions_count, oneCategoryTransactions.size)
                maxSum += sum

                CategoryStatistic(
                    sum = sum,
                    category = context.getString(firstTransaction.category.description),
                    categoryEntry = firstTransaction.category,
                    color = firstTransaction.category.color,
                    moneyColor = firstTransaction.getMoneyColor(walletId).toArgb(),
                    icon = firstTransaction.category.icon,
                    money = getFormattedMoney(currency.code, sum),
                    count = transactionsCount
                )
            }.map {
                it.apply {
                    it.pieEntry = PieEntry((it.sum / maxSum * 100).toInt().toFloat(), it.category + " " + it.money)
                    it.percent = (it.sum / maxSum * 100).toRoundString()
                }
            }.sortedByDescending { it.sum }
    }

    suspend fun getExpenseCategoriesStatistic(
        transaction: List<Transaction>,
        currency: Currency,
        timeIntervalController: TimeIntervalController,
        filterCategories: List<TransactionCategory.ExpenseCategory>
    ): List<CategoryStatistic> {
        val filterCategoriesId = filterCategories.map { it.id }

        return getCategoriesStatistic(currency, 0, transaction) {
            timeIntervalController.isInInterval(it.date) && filterCategoriesId.contains(
                it.category.id
            )
        }
    }

}