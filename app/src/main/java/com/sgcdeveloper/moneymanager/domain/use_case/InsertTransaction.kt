package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import com.sgcdeveloper.moneymanager.data.db.entry.RateEntry
import com.sgcdeveloper.moneymanager.data.db.entry.RecurringTransactionEntry
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.*
import com.sgcdeveloper.moneymanager.domain.repository.CurrencyRepository
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.util.Date
import com.sgcdeveloper.moneymanager.util.toSafeDouble
import javax.inject.Inject

class InsertTransaction @Inject constructor(
    private val context: Context,
    private val moneyManagerRepository: MoneyManagerRepository,
    private val currencyRepository: CurrencyRepository,
    private val insertWallet: InsertWallet,
    private val getWallets: GetWallets,
    private val getRecurringTransactionsUseCase: GetRecurringTransactionsUseCase,
    private val appPreferencesHelper: AppPreferencesHelper
) {
    suspend operator fun invoke(
        transactionId: Long,
        transactionType: TransactionType,
        fromWallet: Wallet,
        toWallet: Wallet? = null,
        description: String,
        amount: String,
        date: Date,
        category: TransactionCategory,
        recurringInterval: RecurringInterval,
        recurringTransactionId: Long
    ): Long {
        val wallets = moneyManagerRepository.getAsyncWallets().associate { it.id to it.currency.code }
        val rates =
            moneyManagerRepository.getRatesOnce() + RateEntry(0, currencyRepository.getDefaultCurrency(), 1.0)

        var toWalletId = 0L
        if (toWallet != null && transactionType == TransactionType.Transfer)
            toWalletId = toWallet.walletId
        val newCategory = if (category.id == None(context).id)
            Transfers(context)
        else
            category

        val fromValue = if (transactionType == TransactionType.Transfer) {
            amount.toDouble() * rates.find { it.currency.code == toWallet!!.currency.code }!!.rate / rates.find { it.currency.code == wallets[toWalletId] }!!.rate
        } else
            0.0

        val toValue = if (transactionType == TransactionType.Transfer) {
            amount.toDouble() * rates.find { it.currency.code == toWallet!!.currency.code }!!.rate / rates.find { it.currency.code == wallets[fromWallet.walletId] }!!.rate
        } else
            0.0

        val transactionEntry = TransactionEntry(
            id = transactionId,
            date = date,
            value = amount.toDouble() ,
            description = description,
            transactionType = transactionType,
            fromWalletId = fromWallet.walletId,
            toWalletId = toWalletId,
            category = newCategory,
            fromTransferValue = fromValue,
            toTransferValue = toValue
        )

        if (recurringInterval.recurring != Recurring.None) {
            insertRecurringTransaction(transactionEntry, recurringInterval, recurringTransactionId)
            return 0
        }

        if (transactionId != 0L)
            cancelTransaction(moneyManagerRepository.getTransaction(transactionId))
        updateWalletMoney(transactionType, transactionEntry, amount.toDouble(), fromWallet.walletId, toWallet?.walletId)

        return moneyManagerRepository.insertTransaction(transactionEntry)
    }

    private suspend fun insertRecurringTransaction(
        entry: TransactionEntry,
        recurringInterval: RecurringInterval,
        recurringTransactionId: Long
    ) {
        moneyManagerRepository.insertRecurringTransaction(
            RecurringTransactionEntry(
                id = recurringTransactionId,
                transactionEntry = entry,
                recurringInterval = recurringInterval,
                fromWalletId = entry.fromWalletId,
                toWalletId = entry.toWalletId
            )
        )
        getRecurringTransactionsUseCase.loadTransactions()
    }

    suspend fun deleteTransaction(transactionId: Long) {
        cancelTransaction(moneyManagerRepository.getTransaction(transactionId))
        moneyManagerRepository.removeTransaction(transactionId)
    }

    suspend fun deleteRecurringTransaction(recurringTransactionId: Long) {
        moneyManagerRepository.removeRecurringTransaction(recurringTransactionId)
    }

    private suspend fun cancelTransaction(transaction: TransactionEntry) {
        updateWalletMoney2(
            transaction.transactionType,
            transaction,
            -transaction.value,
            transaction.fromWalletId,
            transaction.toWalletId
        )
    }

    private suspend fun updateWalletMoney(
        transactionType: TransactionType,
        transaction: TransactionEntry,
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

                insertWallet(
                    if (transaction.fromTransferValue == 0.0)
                        (fromWallet.copy(money = (fromWallet.money.toSafeDouble() + amount * rates.find { it.currency.code == toWallet!!.currency.code }!!.rate / rates.find { it.currency.code == wallets[transaction.toWalletId] }!!.rate).toString()))
                    else
                        fromWallet.copy(money = (fromWallet.money.toSafeDouble() - transaction.fromTransferValue).toString())
                )

                insertWallet(
                    if (transaction.toTransferValue == 0.0)
                        (toWallet.copy(money = (toWallet.money.toSafeDouble() - amount * rates.find { it.currency.code == toWallet.currency.code }!!.rate / rates.find { it.currency.code == wallets[transaction.fromWalletId] }!!.rate).toString()))
                    else
                        toWallet.copy(money = (toWallet.money.toSafeDouble() + transaction.toTransferValue).toString())
                )
            }
        }
    }

    private suspend fun updateWalletMoney2(
        transactionType: TransactionType,
        transaction: TransactionEntry,
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

                insertWallet(
                    if (transaction.fromTransferValue == 0.0)
                        (fromWallet.copy(money = (fromWallet.money.toSafeDouble() - amount * rates.find { it.currency.code == toWallet!!.currency.code }!!.rate / rates.find { it.currency.code == wallets[transaction.toWalletId] }!!.rate).toString()))
                    else
                        fromWallet.copy(money = (fromWallet.money.toSafeDouble() + transaction.fromTransferValue).toString())
                )

                insertWallet(
                    if (transaction.toTransferValue == 0.0)
                        (toWallet.copy(money = (toWallet.money.toSafeDouble() + amount * rates.find { it.currency.code == toWallet.currency.code }!!.rate / rates.find { it.currency.code == wallets[transaction.fromWalletId] }!!.rate).toString()))
                    else
                        toWallet.copy(money = (toWallet.money.toSafeDouble() - transaction.toTransferValue).toString())
                )
            }
        }
    }
}