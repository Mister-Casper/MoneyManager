package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import com.sgcdeveloper.moneymanager.domain.model.BaseTransactionItem
import com.sgcdeveloper.moneymanager.domain.model.Transaction
import com.sgcdeveloper.moneymanager.domain.model.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.use_case.GetWallets.Companion.getCurrencyFormatter
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.util.Date.Companion.getDay
import com.sgcdeveloper.moneymanager.util.Date.Companion.getDayName
import com.sgcdeveloper.moneymanager.util.Date.Companion.getMonthString
import com.sgcdeveloper.moneymanager.util.Date.Companion.toDateString
import com.sgcdeveloper.moneymanager.util.getMoneyColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

class GetTransactionItems @Inject constructor(
    private val context: Context,
    private val moneyManagerRepository: MoneyManagerRepository,
    private val getTransactionsUseCase: GetTransactionsUseCase
) {
    suspend operator fun invoke(wallet: Wallet): List<BaseTransactionItem> = CoroutineScope(Dispatchers.IO).async {
        return@async convertTransactionsToItems(wallet, getTransactionsUseCase(wallet))
    }.await()

    suspend fun getStats(wallet: Wallet): Triple<Int, Int, Int> = CoroutineScope(Dispatchers.IO).async {
        val transactions = getTransactionsUseCase(wallet)
        val income = transactions.filter { it.transactionType == TransactionType.Income }.count()
        val expense = transactions.filter { it.transactionType == TransactionType.Expense }.count()
        val transfer = transactions.filter { it.transactionType == TransactionType.Transfer }.count()
        return@async Triple(income, expense, transfer)
    }.await()

    suspend fun getEntries(wallet: Wallet): List<Transaction> = CoroutineScope(Dispatchers.IO).async {
        return@async getTransactionsUseCase(wallet)
    }.await()

    suspend fun findTransactions(
        wallet: Wallet,
        text: String,
        wallets: List<Wallet>,
        timeIntervalController: TimeIntervalController,
        transactionCategory: List<TransactionCategory>
    ): List<BaseTransactionItem.TransactionItem> = CoroutineScope(Dispatchers.IO).async {
        val walletsId = wallets.map { it.walletId }
        val transactionCategoriesId = transactionCategory.map { it.id }
        val foundedTransactions = invoke(wallet).filterIsInstance<BaseTransactionItem.TransactionItem>()
            .filter {
                (walletsId.isEmpty() || walletsId.contains(it.transactionEntry.fromWalletId) || walletsId.contains(it.transactionEntry.toWalletId))
                        && (transactionCategoriesId.isEmpty() || transactionCategoriesId.contains(it.transactionEntry.category.id))
                        && timeIntervalController.isInInterval(it.transactionEntry.date)
            }
        return@async foundedTransactions.map { transaction->
            var score = 0
            if(transaction.description.lowercase(Locale.getDefault()).contains(text.lowercase()))
                score += 10
            if(transaction.category.lowercase(Locale.getDefault()).contains(text.lowercase()))
                score += 5
            if(transaction.money.lowercase(Locale.getDefault()).contains(text.lowercase()))
                score += 5
            score to transaction
        }.filter { it.first != 0 }.sortedByDescending { it.first }.map { it.second }
    }.await()

    suspend fun getTimeIntervalTransactions(
        wallet: Wallet,
        timeIntervalController: TimeIntervalController,
        transactionCategory: TransactionCategory? = null
    ): List<BaseTransactionItem> = CoroutineScope(Dispatchers.IO).async {
        return@async convertTransactionsToItems(
            wallet,
            getWalletTransactions(wallet, timeIntervalController, transactionCategory)
        )
    }.await()

    private suspend fun getWalletTransactions(
        wallet: Wallet, timeIntervalController: TimeIntervalController,
        transactionCategory: TransactionCategory? = null
    ): List<Transaction> {
        return getTransactionsUseCase(wallet)
            .filter { timeIntervalController.isInInterval(it.date) && (transactionCategory == null || it.category.id == transactionCategory.id) }
    }

    private suspend fun convertTransactionsToItems(
        wallet: Wallet,
        transactions: List<Transaction>
    ): MutableList<BaseTransactionItem> {
        val wallets = moneyManagerRepository.getAsyncWallets().associate { it.id to it.name }
        val items = mutableListOf<BaseTransactionItem>()
        transactions.groupBy {
            val date = it.date.getAsLocalDate()
            StringDate(date, date.toDateString())
        }.forEach { (stringDate, oneDayTransactions) ->
            items.add(
                BaseTransactionItem.TransactionHeader(
                    stringDate.date.getDay(),
                    stringDate.date.getDayName(),
                    stringDate.date.getMonthString(),
                    getTransactionsMoney(wallet, oneDayTransactions),
                )
            )
            oneDayTransactions.forEach { transaction ->
                val moneyColor = transaction.getMoneyColor(wallet.walletId)

                items.add(
                    BaseTransactionItem.TransactionItem(
                        transaction,
                        transaction.category.color,
                        transaction.category.icon,
                        transaction.description,
                        getTransactionDescription(
                            transaction.fromWalletId,
                            transaction.toWalletId,
                            transaction,
                            wallets
                        ),
                        transaction.value,
                        getFormattedMoney(wallet, transaction.value),
                        moneyColor.toArgb()
                    )
                )
            }
        }
        return items
    }

    private fun getTransactionsMoney(wallet: Wallet, transactions: List<Transaction>): String {
        return getFormattedMoney(wallet, transactions.sumOf {
            when (it.transactionType) {
                TransactionType.Income -> it.value
                TransactionType.Expense -> -it.value
                else -> {
                    if (wallet.walletId == it.fromWalletId)
                        -it.value
                    else
                        it.value
                }
            }
        })
    }

    companion object {
        fun getFormattedMoney(wallet: Wallet, money: Double): String {
            val formatter = getCurrencyFormatter(GetWallets.getLocalFromISO(wallet.currency.code)!!)
            return formatter.format(money)
        }

        fun getFormattedMoney(currencyCode: String, money: Double): String {
            val formatter = getCurrencyFormatter(GetWallets.getLocalFromISO(currencyCode)!!)
            return formatter.format(money)
        }
    }

    private fun getTransactionDescription(
        walletFromId: Long,
        walletToId: Long,
        transactionEntry: Transaction,
        wallets: Map<Long, String>
    ): String {
        return if (transactionEntry.transactionType == TransactionType.Transfer) {
            wallets[walletFromId] + " -> " + wallets[walletToId]
        } else
            transactionEntry.category.description
    }

    class StringDate(val date: LocalDate, val string: String) {
        override fun hashCode(): Int {
            return string.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            return string == (other as StringDate).string
        }
    }
}