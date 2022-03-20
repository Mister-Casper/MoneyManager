package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import com.sgcdeveloper.moneymanager.data.db.entry.BudgetEntry
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.BaseBudget
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionItems.Companion.getFormattedMoney
import com.sgcdeveloper.moneymanager.domain.util.BudgetPeriod
import com.sgcdeveloper.moneymanager.domain.util.BudgetPeriod.*
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.util.Date
import com.sgcdeveloper.moneymanager.util.WalletSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

class GetBudgetsUseCase @Inject constructor(
    private val moneyManagerRepository: MoneyManagerRepository,
    private val context: Context,
    private val appPreferencesHelper: AppPreferencesHelper
) {

    suspend operator fun invoke(firstDate: Date = Date(LocalDate.now())): List<BaseBudget> =
        CoroutineScope(Dispatchers.IO).async {
            val budgets: MutableList<BaseBudget> = mutableListOf()
            val transactions = moneyManagerRepository.getTransactionsOnce()
            val budgetEntries = moneyManagerRepository.getBudgetsOnce()
            budgetEntries.sortedBy { it.period.ordinal }.groupBy { it.period }.forEach { periodBudget ->
                val budgetTImeInterval = getTimeIntervalCController(periodBudget.value[0].period, firstDate)
                budgets.add(
                    BaseBudget.BudgetHeader(
                        context.getString(periodBudget.key.periodNameRes),
                        budgetTImeInterval
                    )
                )
                periodBudget.value.forEach { budget ->
                    val spent = getSpent(transactions, budget, budgetTImeInterval)
                    budgets.add(
                        BaseBudget.BudgetItem(
                            budgetEntry = budget,
                            color = budget.color,
                            budgetName = budget.budgetName,
                            spent = getFormattedMoney(
                                WalletSingleton.wallet.value!!,
                                spent
                            ),
                            left = getFormattedMoney(
                                WalletSingleton.wallet.value!!,
                                budget.amount - getSpent(transactions, budget, budgetTImeInterval)
                            ),
                            budget = getFormattedMoney(
                                WalletSingleton.wallet.value!!,
                                budget.amount
                            ),
                            period = context.getString(periodBudget.key.periodNameRes),
                            categories = budget.categories,
                            progress = GetWallets.df.format((spent / budget.amount) * 100).toDouble()
                        )
                    )
                }
            }

            return@async budgets + BaseBudget.AddNewBudget
        }.await()

    private fun getSpent(
        transactions: List<TransactionEntry>,
        budget: BudgetEntry,
        timeIntervalController: TimeIntervalController
    ): Double {
        return transactions.filter { timeIntervalController.isInInterval(it.date) && budget.categories.contains(it.category) && it.transactionType == TransactionType.Expense }
            .sumOf { it.value }
    }

    private fun getStartDate(now: Date, firstDay: DayOfWeek): Date {
        val dif = if (firstDay == now.getAsLocalDate().dayOfWeek)
            0
        else
            kotlin.math.abs(now.getAsLocalDate().dayOfWeek.value - firstDay.value + 7)
        return Date(now.getAsLocalDate().minusDays(dif.toLong()))
    }

    private fun getTimeIntervalCController(period: BudgetPeriod, now: Date): TimeIntervalController {
        return when (period) {
            Daily -> {
                TimeIntervalController.DailyController(now)
            }
            Weekly -> {
                val date = getStartDate(now, appPreferencesHelper.getFirstDayOfWeek())
                TimeIntervalController.WeeklyController(date)
            }
            Monthly -> {
                TimeIntervalController.MonthlyController(now)
            }
            Quarterly -> {
                TimeIntervalController.QuarterlyController(now)
            }
            Yearly -> {
                TimeIntervalController.YearlyController(now)
            }
        }
    }

}