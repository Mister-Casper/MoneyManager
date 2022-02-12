package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.domain.model.BaseTransactionItem
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.theme.red
import com.sgcdeveloper.moneymanager.presentation.theme.white
import kotlinx.coroutines.runBlocking
import java.text.NumberFormat
import javax.inject.Inject

class GetTransactionItems @Inject constructor(
    private val context: Context,
    private val moneyManagerRepository: MoneyManagerRepository
) {
    operator fun invoke(wallet: Wallet): LiveData<List<BaseTransactionItem>> {
        return Transformations.map(moneyManagerRepository.getTransactions(wallet.walletId)) {
            convertTransactionsToItems(wallet, it.sortedByDescending { it.date.epochMillis })
        }
    }

    private fun convertTransactionsToItems(
        wallet: Wallet,
        transactions: List<TransactionEntry>
    ): List<BaseTransactionItem> {
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
                        transaction.category.color,
                        transaction.category.icon,
                        transaction.description,
                        getTransactionDescription(transaction.fromWalletId, transaction.toWalletId, transaction),
                        getFormattedMoney(wallet, transaction.value),
                        moneyColor.toArgb()
                    )
                )
            }
        }
        return items
    }

    private fun getTransactionsMoney(wallet: Wallet, transactions: List<TransactionEntry>): String {
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

    private fun getFormattedMoney(wallet: Wallet, money: Double): String {
        val formatter =
            NumberFormat.getCurrencyInstance(GetWallets.getLocalFromISO(wallet.currency.code)!!)
        return formatter.format(money)
    }

    private fun getTransactionDescription(
        walletFromId: Long,
        walletToId: Long,
        transactionEntry: TransactionEntry
    ): String = runBlocking {
        if (transactionEntry.transactionType == TransactionType.Transfer) {
            moneyManagerRepository.getWallet(walletFromId).name + " -> " + moneyManagerRepository.getWallet(walletToId).name
        } else
            context.getString(transactionEntry.category.description)
    }
}