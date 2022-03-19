package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import com.sgcdeveloper.moneymanager.data.db.entry.BudgetEntry
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.domain.model.BaseBudget
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionItems.Companion.getFormattedMoney
import com.sgcdeveloper.moneymanager.domain.util.BudgetPeriod.*
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.util.Date
import com.sgcdeveloper.moneymanager.util.WalletSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GetBudgetsUseCase @Inject constructor(
    private val moneyManagerRepository: MoneyManagerRepository,
    private val context: Context
) {

    suspend operator fun invoke(firstDate: Date = Date(LocalDate.now())): List<BaseBudget> =
        CoroutineScope(Dispatchers.IO).async {
            val budgets: MutableList<BaseBudget> = mutableListOf()
            val transactions = moneyManagerRepository.getTransactionsOnce()
            val budgetEntries = moneyManagerRepository.getBudgetsOnce()
            budgetEntries.sortedBy { it.period.ordinal }.groupBy { it.period }.forEach { periodBudget ->
                budgets.add(BaseBudget.BudgetHeader(context.getString(periodBudget.key.periodNameRes)))
                periodBudget.value.forEach { budget ->
                    val budgetTImeInterval = getStartDate(budget, firstDate)
                    budgets.add(
                        BaseBudget.BudgetItem(
                            budgetEntry = budget,
                            color = budget.color,
                            budgetName = budget.budgetName,
                            spent = getFormattedMoney(
                                WalletSingleton.wallet.value!!,
                                getSpent(transactions, budget, budgetTImeInterval)
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
                            categories = budget.categories
                        )
                    )
                }
            }

            return@async listOf(BaseBudget.AddNewBudget) + budgets
        }.await()

    private fun getSpent(
        transactions: List<TransactionEntry>,
        budget: BudgetEntry,
        timeIntervalController: TimeIntervalController
    ): Double {
        return transactions.filter { timeIntervalController.isInInterval(it.date) && budget.categories.contains(it.category) && it.transactionType == TransactionType.Expense }
            .sumOf { it.value }
    }

    private fun getStartDate(budget: BudgetEntry, firstDate: Date): TimeIntervalController {
        return when (budget.period) {
            Weekly -> {
                val diff = (firstDate.epochMillis - budget.date.epochMillis) / TimeUnit.DAYS.toMillis(7)
                TimeIntervalController.DailyController(Date(budget.date.getAsLocalDate().plusWeeks(diff)))
            }
            Monthly -> {
                val diff = (firstDate.epochMillis - budget.date.epochMillis) / TimeUnit.DAYS.toMillis(30)
                TimeIntervalController.MonthlyController(Date(budget.date.getAsLocalDate().plusMonths(diff)))
            }
            Quarterly -> {
                val diff = (firstDate.epochMillis - budget.date.epochMillis) / TimeUnit.DAYS.toMillis(120)
                TimeIntervalController.QuarterlyController(Date(budget.date.getAsLocalDate().plusMonths(diff * 4)))
            }
            Yearly -> {
                val diff = (firstDate.epochMillis - budget.date.epochMillis) / TimeUnit.DAYS.toMillis(365)
                TimeIntervalController.YearlyController(Date(budget.date.getAsLocalDate().plusYears(diff)))
            }
        }
    }

}