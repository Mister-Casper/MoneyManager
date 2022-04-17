package com.sgcdeveloper.moneymanager.domain.use_case

import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.BaseTransactionItem
import com.sgcdeveloper.moneymanager.domain.model.Transaction
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.model.calendar.CalendarDay
import com.sgcdeveloper.moneymanager.domain.model.calendar.DayTransactions
import com.sgcdeveloper.moneymanager.domain.model.calendar.TransactionsCalendar
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionItems.Companion.getFormattedMoney
import com.sgcdeveloper.moneymanager.util.getTransactionsExpense
import com.sgcdeveloper.moneymanager.util.getTransactionsIncome
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.*
import javax.inject.Inject

class GetTransactionsCalendarUseCase @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val appPreferencesHelper: AppPreferencesHelper,
    private val getTransactionItems: GetTransactionItems
) {

    suspend operator fun invoke(wallet: Wallet, timeIntervalController: TimeIntervalController): TransactionsCalendar =
        CoroutineScope(Dispatchers.IO).async {
            val transactions = getTransactionsUseCase(wallet)
            val monthTransactions = transactions.filter { timeIntervalController.isInInterval(it.date) }
                .groupBy { it.date.getAsLocalDate().dayOfMonth }
            val incomeNum = monthTransactions.values.getTransactionsIncome(wallet)
            val expenseNum = monthTransactions.values.getTransactionsExpense(wallet)
            val income = getFormattedMoney(wallet, incomeNum)
            val expense = getFormattedMoney(wallet, expenseNum)
            val total = getFormattedMoney(wallet ,(incomeNum + expenseNum))

            return@async TransactionsCalendar(
                income,
                expense,
                total,
                getWeek(),
                getCalendarDays(
                    wallet,
                    timeIntervalController,
                    monthTransactions
                )
            )
        }.await()

    private suspend fun getCalendarDays(
        wallet: Wallet,
        timeIntervalController: TimeIntervalController,
        dayTransactions: Map<Int, List<Transaction>>
    ): List<CalendarDay> {
        val today = LocalDateTime.now().dayOfMonth
        val startDay = timeIntervalController.getStartDate().getAsLocalDate().withDayOfMonth(1).dayOfWeek.value
        val calendarDays = mutableListOf<CalendarDay>()
        val daysInMonth = timeIntervalController.getStartDate().getAsLocalDate().lengthOfMonth()
        val maxDay = dayTransactions.keys.maxOfOrNull { it } ?: daysInMonth
        var existDayNum = 1
        for (i in 1..42) {
            if (i < startDay || existDayNum > maxDay) {
                var money = ""
                val dayNum = if (existDayNum in startDay..daysInMonth) {
                    existDayNum += 1
                    money = "0"
                    (existDayNum - 1).toString()
                } else ""
                calendarDays.add(CalendarDay(false, dayNum, false, isHoliday(i), money, money, DayTransactions()))
            } else {
                val dailyIncome = (dayTransactions[existDayNum]?.getTransactionsIncome(wallet)?.toInt() ?: 0).toString()
                val dailyExpense =
                    (dayTransactions[existDayNum]?.getTransactionsExpense(wallet)?.toInt() ?: 0).toString()
                val total = (dailyIncome.toInt() + dailyExpense.toInt()).toString()

                val dayTitle =
                    timeIntervalController.getStartDate().getAsLocalDate().withDayOfMonth(existDayNum).toString()

                calendarDays.add(
                    CalendarDay(
                        true,
                        existDayNum.toString(),
                        today == existDayNum,
                        isHoliday(i),
                        dailyIncome,
                        dailyExpense,
                        DayTransactions(
                            dayTitle,
                            total,
                            getTransactionItems.convertTransactionsToItems(
                                wallet,
                                dayTransactions[existDayNum] ?: Collections.emptyList()
                            )
                                .filterIsInstance<BaseTransactionItem.TransactionItem>()
                        )
                    )
                )
                existDayNum += 1
            }
        }

        return calendarDays
    }

    private fun isHoliday(dayNum: Int): Boolean {
        val day = 1 + (((dayNum - 1) % 7))
        return day == 6 && day == 7
    }

    private fun getWeek(): List<String> {
        val firstDay = appPreferencesHelper.getFirstDayOfWeek()
        val days = mutableListOf<DayOfWeek>()
        for (i in 0..6) {
            val dayNumber = 1 + ((firstDay.value - 1 + i) % 7)
            val day = DayOfWeek.of(dayNumber)
            days.add(day)
        }
        return days.map { it.getDisplayName(TextStyle.SHORT, Locale.getDefault()) }
    }
}