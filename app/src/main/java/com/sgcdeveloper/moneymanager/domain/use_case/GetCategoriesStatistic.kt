package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import com.github.mikephil.charting.data.PieEntry
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.domain.model.CategoryStatistic
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionItems.Companion.getFormattedMoney
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import javax.inject.Inject

class GetCategoriesStatistic @Inject constructor(
    private val context: Context,
    private val moneyManagerRepository: MoneyManagerRepository
) {

    fun getExpenseStatistic(transactions: List<TransactionEntry>, wallet: Wallet): List<CategoryStatistic> {
        return getCategoriesStatistic(
            wallet,
            transactions
        ) {return@getCategoriesStatistic it.transactionType == TransactionType.Expense || (it.transactionType == TransactionType.Transfer && it.fromWalletId == wallet.walletId) }
    }

    fun getIncomeStatistic(transactions: List<TransactionEntry>, wallet: Wallet): List<CategoryStatistic> {
        return getCategoriesStatistic(wallet, transactions,
        ) {return@getCategoriesStatistic it.transactionType == TransactionType.Income || (it.transactionType == TransactionType.Transfer && it.toWalletId == wallet.walletId) }
    }

    private fun getCategoriesStatistic(
        wallet: Wallet,
        transaction: List<TransactionEntry>,
        filter: (it: TransactionEntry) -> Boolean
    ): List<CategoryStatistic> {
        val categories = mutableListOf<CategoryStatistic>()

        var maxSum = 0.0

        transaction.filter { filter(it) }
            .groupBy { it.category.icon }.values.forEach { oneCategoryTransactions ->
                val firstTransaction = oneCategoryTransactions[0]
                var sum = 0.0
                oneCategoryTransactions.forEach { transaction ->
                    sum += transaction.value
                }
                categories.add(
                    CategoryStatistic(
                        sum = sum,
                        category = context.getString(firstTransaction.category.description),
                        color = firstTransaction.category.color,
                        money = getFormattedMoney(wallet, sum)
                    )
                )
                maxSum += sum
            }

        categories.forEach {
            it.pieEntry = PieEntry((it.sum / maxSum * 100).toInt().toFloat(), it.category + " " + it.money)
            it.percent = it.sum / maxSum * 10
        }

        return categories
    }

}