package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import com.github.mikephil.charting.data.BarEntry
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.domain.model.DayStatistic
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.model.WeeklyStatistic
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionItems.Companion.getFormattedMoney
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import com.sgcdeveloper.moneymanager.presentation.theme.red
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.util.Date
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.time.DayOfWeek
import javax.inject.Inject

class GetWeeklyStatistic @Inject constructor(
    private val context: Context,
    private val moneyManagerRepository: MoneyManagerRepository
) {

    suspend operator fun invoke(
        context: Context,
        wallet: Wallet,
        type: TransactionType,
        startDate: Date,
        timeIntervalController: TimeIntervalController
    ): WeeklyStatistic =
        CoroutineScope(Dispatchers.IO).async {
            val daysStatistic = LinkedHashMap<Int, DayStatistic>()
            val daysStatisticMap = LinkedHashMap<Date, MutableList<TransactionEntry>>()
            val daysMap = getWeek(startDate).toSet()
            val transactions =
                moneyManagerRepository.getWalletTransactions(wallet.walletId).sortedBy { it.date.epochMillis }
            daysMap.forEach {
                daysStatisticMap[Date(it.getAsLocalDate())] = ArrayList()
            }
            for (transaction in transactions) {
                val transactionDate = transaction.date
                if (Date(transactionDate.getAsLocalDate()) in daysMap && transaction.transactionType == type && timeIntervalController.isInInterval(
                        transaction.date
                    )
                ) {
                    daysStatisticMap[Date(transactionDate.getAsLocalDate())]!!.add(transaction)
                }
            }

            daysStatisticMap.onEachIndexed { i, dayValue ->
                val day = dayValue.value
                val dif = if (dayValue.key.getAsLocalDate() == startDate.getAsLocalDate())
                    0
                else
                    kotlin.math.abs(dayValue.key.getAsLocalDate().dayOfWeek.value + 7 - startDate.getAsLocalDate().dayOfWeek.value)
                val transactionsCount = if (day.size == 1)
                    context.getString(R.string.one_transaction)
                else
                    context.getString(R.string.transactions_count, day.size)
                daysStatistic[dif] = DayStatistic(
                    dayValue.key.getDayName(),
                    dayValue.key.toDayMonthString(),
                    getFormattedMoney(wallet, day.sumOf { it.value }),
                    (if (type == TransactionType.Expense) red else white).toArgb(),
                    transactionsCount,
                    BarEntry(i.toFloat(), day.sumOf { it.value }.toFloat())
                )
            }

            val (title, rowColor) = if (type == TransactionType.Expense) {
                Pair(context.getString(R.string.weekly_spending), red.toArgb())
            } else {
                Pair(context.getString(R.string.weekly_earning), blue.toArgb())
            }

            return@async WeeklyStatistic(
                daysStatistic.toSortedMap().map { it.value },
                getFormattedMoney(
                    wallet,
                    daysStatisticMap.values.sumOf { it.sumOf { transaction -> transaction.value } }),
                title,
                daysMap.map { it.getDayName().substring(0..2) }, rowColor
            )
        }.await()

    private fun getWeek(firstDay: Date): List<Date> {
        return List(7) { index -> Date(firstDay.getAsLocalDate().plusDays(index.toLong())) }
    }

    fun getStartDate(now: Date, firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY): Date {
        val dif = kotlin.math.abs(now.getAsLocalDate().dayOfWeek.value - firstDayOfWeek.value + 7)
        return Date(now.getAsLocalDate().minusDays(dif.toLong()))
    }
}