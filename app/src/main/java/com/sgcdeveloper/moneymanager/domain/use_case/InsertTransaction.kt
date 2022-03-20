package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import com.sgcdeveloper.moneymanager.data.db.entry.RateEntry
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.repository.CurrencyRepository
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.util.Date
import com.sgcdeveloper.moneymanager.util.SyncHelper
import com.sgcdeveloper.moneymanager.util.WalletSingleton
import com.sgcdeveloper.moneymanager.util.toSafeDouble
import javax.inject.Inject

class InsertTransaction @Inject constructor(
    private val context: Context,
    private val moneyManagerRepository: MoneyManagerRepository,
    private val currencyRepository: CurrencyRepository,
    private val insertWallet: InsertWallet,
    private val getWallets: GetWallets,
    private val syncHelper: SyncHelper
) {
    suspend operator fun invoke(
        transactionId: Long,
        transactionType: TransactionType,
        fromWallet: Wallet,
        toWallet: Wallet? = null,
        description: String,
        amount: String,
        date: Date,
        category: TransactionCategory
    ): Long {
        var toWalletId = 0L
        if (toWallet != null && transactionType == TransactionType.Transfer)
            toWalletId = toWallet.walletId
        if (transactionId != 0L)
            cancelTransaction(moneyManagerRepository.getTransaction(transactionId))
        val newCategory = if (category == TransactionCategory.None)
            TransactionCategory.Transfers
        else
            category

        updateWalletMoney(transactionType, amount.toDouble(), fromWallet.walletId, toWallet?.walletId)

        val transactionId = moneyManagerRepository.insertTransaction(
            TransactionEntry(
                id = transactionId,
                date = date,
                value = amount.toDouble(),
                description = description,
                transactionType = transactionType,
                fromWalletId = fromWallet.walletId,
                toWalletId = toWalletId,
                category = newCategory
            )
        )
        syncHelper.syncServerData()
        return transactionId
    }

    suspend fun deleteTransaction(transactionId: Long) {
        cancelTransaction(moneyManagerRepository.getTransaction(transactionId))
        moneyManagerRepository.removeTransaction(transactionId)
        syncHelper.syncServerData()
    }

    suspend fun cancelTransactions(transactions: List<TransactionEntry>, walletId: Long) {
        val wallets = moneyManagerRepository.getAsyncWallets().associate { it.id to it.currency.code }
        val rates =
            moneyManagerRepository.getRatesOnce() + RateEntry(0, currencyRepository.getDefaultCurrency(), 1.0)

        val newWallets: MutableList<Wallet> = ArrayList()

        transactions.forEach { transaction ->
            val fromWallet = newWallets.find { it.walletId == transaction.fromWalletId } ?: getWallets.getWallet(
                transaction.fromWalletId
            )
            val toWallet = newWallets.find { it.walletId == transaction.fromWalletId } ?: getWallets.getWallet(
                transaction.toWalletId
            )
            val amount = transaction.value

            when (transaction.transactionType) {
                TransactionType.Expense -> {
                    newWallets.remove(fromWallet)
                    newWallets.add(fromWallet.copy(money = (fromWallet.money.toSafeDouble() - amount).toString()))
                }
                TransactionType.Income -> {
                    newWallets.remove(fromWallet)
                    newWallets.add(fromWallet.copy(money = (fromWallet.money.toSafeDouble() + amount).toString()))
                }
                TransactionType.Transfer -> {
                    newWallets.remove(fromWallet)
                    newWallets.remove(toWallet)
                    newWallets.add(fromWallet.copy(money = (fromWallet.money.toSafeDouble() - amount).toString()))
                    newWallets.add(toWallet.copy(money = (toWallet.money.toSafeDouble() + amount * rates.find { it.currency.code == toWallet.currency.code }!!.rate / rates.find { it.currency.code == wallets[transaction.fromWalletId] }!!.rate).toString()))
                }
            }
        }

        if (WalletSingleton.wallet.value?.walletId == walletId)
            WalletSingleton.wallet.value = null
        insertWallet.insertWallets(newWallets)
        moneyManagerRepository.removeWalletTransactions(walletId)
        moneyManagerRepository.removeWallet(walletId)
        syncHelper.syncServerData()
    }

    suspend fun cancelTransaction(transaction: TransactionEntry) {
        updateWalletMoney(
            transaction.transactionType, -transaction.value, transaction.fromWalletId, transaction.toWalletId
        )
    }

    private suspend fun updateWalletMoney(
        transactionType: TransactionType,
        amount: Double,
        fromWalletId: Long,
        toWalletId: Long?
    ) {
        val wallets = moneyManagerRepository.getAsyncWallets().associate { it.id to it.currency.code }
        val rates = moneyManagerRepository.getRatesOnce() + RateEntry(0, currencyRepository.getDefaultCurrency(), 1.0)

        val fromWallet = getWallets.getWallet(fromWalletId)
        when (transactionType) {
            TransactionType.Expense -> {
                insertWallet(fromWallet.copy(money = (fromWallet.money.toSafeDouble() - amount).toString()))
            }
            TransactionType.Income -> {
                insertWallet(fromWallet.copy(money = (fromWallet.money.toSafeDouble() + amount).toString()))
            }
            TransactionType.Transfer -> {
                val toWallet = getWallets.getWallet(toWalletId!!)
                insertWallet(fromWallet.copy(money = (fromWallet.money.toSafeDouble() - amount).toString()))
                insertWallet(toWallet.copy(money = (toWallet.money.toSafeDouble() + amount * rates.find { it.currency.code == toWallet.currency.code }!!.rate / rates.find { it.currency.code == wallets[fromWalletId] }!!.rate).toString()))
            }
        }
    }
}