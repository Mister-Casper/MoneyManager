package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.domain.model.BaseTransactionItem
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.theme.red
import com.sgcdeveloper.moneymanager.presentation.theme.white
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.text.NumberFormat
import javax.inject.Inject

class GetTransactionItems @Inject constructor(
    private val context: Context,
    private val moneyManagerRepository: MoneyManagerRepository
) {
    suspend operator fun invoke(wallet: Wallet): List<BaseTransactionItem> = CoroutineScope(Dispatchers.IO).async {
        return@async convertTransactionsToItems(
            wallet,
            moneyManagerRepository.getWalletTransactions(wallet.walletId).sortedByDescending { it.date.epochMillis })
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
    ): List<TransactionEntry> = CoroutineScope(Dispatchers.IO).async {
        return@async moneyManagerRepository.getWalletTransactions(wallet.walletId)
            .filter { timeIntervalController.isInInterval(it.date) && (transactionCategory == null || it.category.id == transactionCategory.id) }
            .sortedByDescending { it.date.epochMillis }
    }.await()

    private suspend fun convertTransactionsToItems(
        wallet: Wallet,
        transactions: List<TransactionEntry>
    ): MutableList<BaseTransactionItem> = CoroutineScope(Dispatchers.IO).async {
        val items = mutableListOf<BaseTransactionItem>()
        transactions.groupBy { it.date.toDateString() }.values.forEach { oneDayTransactions ->
            val date = oneDayTransactions.first().date
            items.add(
                BaseTransactionItem.TransactionHeader(
                    date.getDay(),
                    date.getDayName(),
                    date.getMonth(),
                    getTransactionsMoney(wallet, oneDayTransactions)
                )
            )
            oneDayTransactions.forEach { transaction ->
                val moneyColor =
                    if (transaction.transactionType == TransactionType.Expense) red else if (transaction.transactionType == TransactionType.Income) white else {
                        if (transaction.fromWalletId == wallet.walletId)
                            red
                        else
                            white
                    }

                items.add(
                    BaseTransactionItem.TransactionItem(
                        transaction,
                        transaction.category.color,
                        transaction.category.icon,
                        transaction.description,
                        getTransactionDescription(
                            transaction.fromWalletId,
                            transaction.toWalletId,
                            transaction
                        ),
                        transaction.value,
                        getFormattedMoney(wallet, transaction.value),
                        moneyColor.toArgb()
                    )
                )
            }
        }
        return@async items
    }.await()

    private suspend fun getTransactionsMoney(wallet: Wallet, transactions: List<TransactionEntry>): String =
        CoroutineScope(Dispatchers.IO).async {
            return@async getFormattedMoney(wallet, transactions.sumOf {
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
        }.await()

    companion object {
        suspend fun getFormattedMoney(wallet: Wallet, money: Double): String = CoroutineScope(Dispatchers.IO).async {
            val formatter =
                NumberFormat.getCurrencyInstance(GetWallets.getLocalFromISO(wallet.currency.code)!!)
            return@async formatter.format(money)
        }.await()
    }

    private suspend fun getTransactionDescription(
        walletFromId: Long,
        walletToId: Long,
        transactionEntry: TransactionEntry
    ): String = CoroutineScope(Dispatchers.IO).async {
        return@async if (transactionEntry.transactionType == TransactionType.Transfer) {
            moneyManagerRepository.getWallet(walletFromId).name + " -> " + moneyManagerRepository.getWallet(walletToId).name
        } else
            context.getString(transactionEntry.category.description)
    }.await()
}