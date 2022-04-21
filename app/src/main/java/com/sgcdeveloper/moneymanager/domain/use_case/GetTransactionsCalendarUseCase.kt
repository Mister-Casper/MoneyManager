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
import com.sgcdeveloper.moneymanager.util.Date.Companion.getShortDayName
import com.sgcdeveloper.moneymanager.util.Date.Companion.toDateString
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
            val total = getFormattedMoney(wallet, (incomeNum + expenseNum))

            return@async TransactionsCalendar(
                income,
                expense,
                total,
                getWeek(),
                getCalendarDays(
                    wallet,
                    timeIntervalController,
                    monthTransactions,
                    appPreferencesHelper.getFirstDayOfWeek()
                )
            )
        }.await()

    private suspend fun getCalendarDays(
        wallet: Wallet,
        timeIntervalController: TimeIntervalController,
        dayTransactions: Map<Int, List<Transaction>>,
        firstDayOfWeek: DayOfWeek
    ): List<CalendarDay> {
        val today = LocalDateTime.now().dayOfMonth
        val startDay = (timeIntervalController.getStartDate().getAsLocalDate()
            .withDayOfMonth(1).dayOfWeek.value + (7 - firstDayOfWeek.value) + 1) % 7
        val calendarDays = mutableListOf<CalendarDay>()
        val daysInMonth = timeIntervalController.getStartDate().getAsLocalDate().lengthOfMonth()
        val maxDay = dayTransactions.keys.maxOfOrNull { it } ?: daysInMonth
        var existDayNum = 1
        var day = timeIntervalController.getStartDate().getAsLocalDate().withDayOfMonth(existDayNum)
        for (i in 1..42) {
            if (existDayNum <= day.lengthOfMonth())
                day = timeIntervalController.getStartDate().getAsLocalDate().withDayOfMonth(existDayNum)
            val dayStr = day.getShortDayName() + " " + day.toDateString()
            if (i < startDay || existDayNum > maxDay) {
                var money = ""
                var dayTitle = ""
                val dayNum = if (existDayNum in startDay..daysInMonth) {
                    dayTitle = dayStr
                    existDayNum += 1
                    money = "0"
                    (existDayNum - 1).toString()
                } else ""
                calendarDays.add(
                    CalendarDay(
                        money != "",
                        dayNum,
                        false,
                        isHoliday(i,appPreferencesHelper.getFirstDayOfWeek().value),
                        money,
                        money,
                        DayTransactions(dayNumber = i, dayText = dayTitle)
                    )
                )
            } else {
                val dailyIncomeNum = (dayTransactions[existDayNum]?.getTransactionsIncome(wallet) ?: 0).toDouble()
                val dailyExpenseNum = (dayTransactions[existDayNum]?.getTransactionsExpense(wallet) ?: 0).toDouble()
                val dailyIncome = dailyIncomeNum.toInt().toString()
                val dailyExpense = dailyExpenseNum.toInt().toString()
                val total = getFormattedMoney(wallet, dailyExpenseNum + dailyIncomeNum)

                calendarDays.add(
                    CalendarDay(
                        true,
                        existDayNum.toString(),
                        today == existDayNum,
                        isHoliday(i,appPreferencesHelper.getFirstDayOfWeek().value),
                        dailyIncome,
                        dailyExpense,
                        DayTransactions(
                            i,
                            dayStr,
                            day,
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

    private fun isHoliday(dayNum: Int,startDay:Int): Boolean {
        val day = (((dayNum + startDay - 1) % 7))
        return day == 6 || day == 0
    }

    private fun getWeek(): List<com.sgcdeveloper.moneymanager.domain.model.calendar.DayOfWeek> {
        val firstDay = appPreferencesHelper.getFirstDayOfWeek()
        val days = mutableListOf<DayOfWeek>()
        for (i in 0..6) {
            val dayNumber = 1 + ((firstDay.value - 1 + i) % 7)
            val day = DayOfWeek.of(dayNumber)
            days.add(day)
        }
        return days.map { com.sgcdeveloper.moneymanager.domain.model.calendar.DayOfWeek(it.getDisplayName(TextStyle.SHORT, Locale.getDefault()),it.value == 6 || it.value == 7) }
    }
}