package com.sgcdeveloper.moneymanager.domain.use_case

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.sgcdeveloper.moneymanager.data.db.entry.RateEntry
import com.sgcdeveloper.moneymanager.data.db.entry.RecurringTransactionEntry
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.domain.model.AddRecurringTransaction
import com.sgcdeveloper.moneymanager.domain.model.BaseRecurringTransaction
import com.sgcdeveloper.moneymanager.domain.model.Recurring.*
import com.sgcdeveloper.moneymanager.domain.model.RecurringInterval
import com.sgcdeveloper.moneymanager.domain.model.RecurringTransaction
import com.sgcdeveloper.moneymanager.domain.repository.CurrencyRepository
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionItems.Companion.getFormattedMoney
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.theme.red
import com.sgcdeveloper.moneymanager.util.Date
import com.sgcdeveloper.moneymanager.util.toSafeDouble
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class GetRecurringTransactionsUseCase @Inject constructor(
    private val moneyManagerRepository: MoneyManagerRepository,
    private val currencyRepository: CurrencyRepository,
    private val insertWallet: InsertWallet,
    private val getWallets: GetWallets
) {
    suspend operator fun invoke(): List<BaseRecurringTransaction> = CoroutineScope(Dispatchers.IO).async {
        return@async moneyManagerRepository.getRecurringTransactionsOnce().map {
            val lastDate = (it.recurringInterval.lastTransactionDate ?: it.transactionEntry.date).getAsLocalDate()
            RecurringTransaction(
                it.id,
                it.transactionEntry,
                it.recurringInterval,
                it.fromWalletId,
                it.toWalletId,
                Date(lastDate).toDateString(),
                getFormattedMoney(
                    moneyManagerRepository.getWallet(it.fromWalletId).currency.code,
                    it.transactionEntry.value
                ),
                (if (it.transactionEntry.transactionType == TransactionType.Expense) red else Color.Unspecified).toArgb()
            )
        } + listOf(AddRecurringTransaction)
    }.await()

    private fun getNextTransactionDate(
        date: LocalDate,
        recurringTransactionEntry: RecurringTransactionEntry
    ): LocalDate {
        return when (recurringTransactionEntry.recurringInterval) {
            is RecurringInterval.Daily -> {
                date.plusDays(recurringTransactionEntry.recurringInterval.repeatInterval.toLong())
            }
            is RecurringInterval.Monthly -> {
                val newDate =
                    date.plusMonths(recurringTransactionEntry.recurringInterval.repeatInterval.toLong())
                if (recurringTransactionEntry.recurringInterval.sameDay == -1) {
                    newDate.withDayOfMonth(newDate.lengthOfMonth())
                } else {
                    newDate.withDayOfMonth(
                        kotlin.math.min(
                            recurringTransactionEntry.recurringInterval.sameDay,
                            newDate.lengthOfMonth()
                        )
                    )
                }
            }
            RecurringInterval.None -> throw Exception("Impossible shit exception")
            is RecurringInterval.Weekly -> {
                var nextDay = date
                nextDay = nextDay.plusDays(1)
                while (!recurringTransactionEntry.recurringInterval.days.contains(nextDay.dayOfWeek)) {
                    nextDay = nextDay.plusDays(1)
                }
                nextDay
            }
            is RecurringInterval.Yearly -> {
                date.plusYears(recurringTransactionEntry.recurringInterval.repeatInterval.toLong())
            }
            else -> throw Exception("Impossible shit exception")
        }
    }

    suspend fun loadTransactions() = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
        val recurringTransactions = moneyManagerRepository.getRecurringTransactionsOnce().toMutableList()
        if (recurringTransactions.isEmpty())
            return@withContext
        val newRecurringTransactions = mutableListOf<RecurringTransactionEntry>()
        val newTransactions = mutableListOf<TransactionEntry>()
        recurringTransactions.forEach { transaction ->
            val firstTransactionDate =
                transaction.recurringInterval.lastTransactionDate ?: transaction.transactionEntry.date
            val now = Date(LocalDateTime.now())
            var newTransactionDate = if (isAvailableDate(
                    firstTransactionDate,
                    transaction.recurringInterval
                )
            ) firstTransactionDate else Date(
                getNextTransactionDate(
                    firstTransactionDate.getAsLocalDate(),
                    transaction
                )
            )

            while (newTransactionDate <= now) {
                if (newTransactionDate > transaction.recurringInterval.endDate && !transaction.recurringInterval.isForever) {
                    newRecurringTransactions.remove(transaction)
                    break
                }
                val newTransaction = transaction.transactionEntry.copy(date = newTransactionDate)
                newTransactions.add(newTransaction)
                newTransactionDate = Date(getNextTransactionDate(newTransactionDate.getAsLocalDate(), transaction))
            }
            if (recurringTransactions.contains(transaction))
                newRecurringTransactions.add(transaction.copy(recurringInterval = transaction.recurringInterval.apply {
                    this.lastTransactionDate = newTransactionDate
                }))
        }
        moneyManagerRepository.deleteAllRecurringTransactions()
        moneyManagerRepository.insertRecurringTransactions(newRecurringTransactions)
        insertTransactions(newTransactions)
        moneyManagerRepository.insertTransactions(newTransactions)
    }

    private suspend fun insertTransactions(
        transactions: List<TransactionEntry>
    ) {
        transactions.forEach { transaction ->
            updateWalletMoney(
                transaction.transactionType,
                transaction.value,
                transaction.fromWalletId,
                transaction.toWalletId
            )
        }
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

    private fun isAvailableDate(date: Date, recurringInterval: RecurringInterval): Boolean {
        return when (recurringInterval.recurring) {
            None -> throw Exception("")
            Daily -> true
            Weekly -> recurringInterval.days.contains(date.getAsLocalDate().dayOfWeek)
            Monthly -> true
            Yearly -> true
        }
    }

}