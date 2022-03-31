package com.sgcdeveloper.moneymanager.domain.use_case

import androidx.compose.ui.graphics.toArgb
import com.sgcdeveloper.moneymanager.data.db.entry.RecurringTransactionEntry
import com.sgcdeveloper.moneymanager.domain.model.AddRecurringTransaction
import com.sgcdeveloper.moneymanager.domain.model.BaseRecurringTransaction
import com.sgcdeveloper.moneymanager.domain.model.RecurringInterval
import com.sgcdeveloper.moneymanager.domain.model.RecurringTransaction
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionItems.Companion.getFormattedMoney
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.theme.red
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.util.Date
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.time.LocalDate
import javax.inject.Inject

class GetRecurringTransactionsUseCase @Inject constructor(
    private val moneyManagerRepository: MoneyManagerRepository
) {

    suspend operator fun invoke(): List<BaseRecurringTransaction> = CoroutineScope(Dispatchers.IO).async {
        return@async moneyManagerRepository.getRecurringTransactionsOnce().map {
            val lastDate = (it.recurringInterval.lastTransactionDate?:it.transactionEntry.date).getAsLocalDate()
            RecurringTransaction(
                it.id,
                it.transactionEntry,
                it.recurringInterval,
                it.fromWalletId,
                it.toWalletId,
                Date(getNextTransactionDate(lastDate, it)).toDateString(),
                getFormattedMoney(moneyManagerRepository.getWallet(it.fromWalletId).currency.code,it.transactionEntry.value),
                (if(it.transactionEntry.transactionType == TransactionType.Expense) red else white).toArgb()
            )
        } + listOf(AddRecurringTransaction)
    }.await()

    private suspend fun getNextTransactionDate(
        date: LocalDate,
        recurringTransactionEntry: RecurringTransactionEntry
    ): LocalDate =
        CoroutineScope(Dispatchers.IO).async {
            return@async when (recurringTransactionEntry.recurringInterval) {
                is RecurringInterval.Daily -> {
                    date.plusDays(1)
                }
                is RecurringInterval.Monthly -> {
                    if (recurringTransactionEntry.recurringInterval.sameDay == -1) {
                        date.plusMonths(1)
                    } else {
                        if (date.dayOfMonth == date.lengthOfMonth()) {
                            val newDate = date.plusMonths(1)
                            newDate.withDayOfMonth(kotlin.math.min(recurringTransactionEntry.recurringInterval.sameDay,newDate.lengthOfMonth()))
                        } else {
                            date.withDayOfMonth(date.lengthOfMonth())
                        }
                    }
                }
                RecurringInterval.None -> throw Exception("Impossible shit exception")
                is RecurringInterval.Weekly -> {
                    var nextDay = date
                    while (!recurringTransactionEntry.recurringInterval.days.contains(nextDay.dayOfWeek)) {
                        nextDay = nextDay.plusDays(1)
                    }
                    nextDay
                }
                is RecurringInterval.Yearly -> {
                    date.plusYears(1)
                }
                else -> throw Exception("Impossible shit exception")
            }
        }.await()

}