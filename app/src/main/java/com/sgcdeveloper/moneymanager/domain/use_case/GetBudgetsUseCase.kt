package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.data.db.entry.BudgetEntry
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.BaseBudget
import com.sgcdeveloper.moneymanager.domain.model.BudgetGraphEntry
import com.sgcdeveloper.moneymanager.domain.model.Transaction
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionItems.Companion.getFormattedMoney
import com.sgcdeveloper.moneymanager.domain.use_case.GetWallets.Companion.df
import com.sgcdeveloper.moneymanager.domain.util.BudgetPeriod
import com.sgcdeveloper.moneymanager.domain.util.BudgetPeriod.*
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.util.Date
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.text.NumberFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max

class GetBudgetsUseCase @Inject constructor(
    private val moneyManagerRepository: MoneyManagerRepository,
    private val context: Context,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val appPreferencesHelper: AppPreferencesHelper,
    private val getCategoriesStatistic: GetCategoriesStatistic,
    private val getTransactionCategoriesUseCase: GetTransactionCategoriesUseCase
) {

    private val nf = NumberFormat.getInstance(Locale.getDefault())

    suspend operator fun invoke(
        firstDate: Date = Date(LocalDate.now()),
        period: BudgetPeriod? = null
    ): List<BaseBudget> =
        CoroutineScope(Dispatchers.IO).async {
            val budgets: MutableList<BaseBudget> = mutableListOf()
            val transactions = getTransactionsUseCase().sortedBy { it.date.epochMillis }
            val budgetEntries = moneyManagerRepository.getAsyncWBudgets()
            val categories = getTransactionCategoriesUseCase.getAllExpenseItems()
            budgetEntries.sortedBy { it.period.ordinal }.groupBy { it.period }
                .filter { period == null || it.key.ordinal == period.ordinal }.forEach { periodBudget ->
                    val budgetTImeInterval = getTimeIntervalCController(periodBudget.value[0].period, firstDate)
                    val spents = mutableListOf<Double>()
                    periodBudget.value.forEach { budget ->
                        if (budget.categories.map { it.id }.contains(0)) {
                            budget.categories = categories
                        }
                        spents.add(getSpent(transactions, budget, budgetTImeInterval))
                    }
                    if (period == null)
                        budgets.add(
                            BaseBudget.BudgetHeader(
                                context.getString(periodBudget.key.periodNameRes),
                                budgetTImeInterval,
                                context.getString(
                                    R.string.total_budget,
                                    getFormattedMoney(appPreferencesHelper.getDefaultCurrency()!!.code, spents.sum()),
                                    getFormattedMoney(
                                        appPreferencesHelper.getDefaultCurrency()!!.code,
                                        periodBudget.value.sumOf { it.amount }
                                    )
                                ),
                                context.getString(periodBudget.key.fullNameRes),
                                budgetTImeInterval.getDescription(),
                                periodBudget.key
                            )
                        )
                    periodBudget.value.forEachIndexed { i, budget ->
                        val categoryDescription =
                            if (budget.categories.map { it.id }.contains(0)) {
                                budget.categories = categories
                                context.getString(R.string.all_category)
                            } else
                                budget.categories.filter { it.id != 0L }.joinToString(separator = ", ") {it.description}
                        val spent = spents[i]
                        val progress = kotlin.math.min(1.0, (spent / budget.amount))
                        val left = budget.amount - getSpent(transactions, budget, budgetTImeInterval)
                        val leftStrRes = if (left >= 0) R.string.remain else R.string.overspent

                        budgets.add(
                            BaseBudget.BudgetItem(
                                budgetEntry = budget,
                                color = budget.color,
                                budgetName = budget.budgetName,
                                spent = getFormattedMoney(
                                    appPreferencesHelper.getDefaultCurrency()!!.code,
                                    spent
                                ),
                                left = getFormattedMoney(
                                    appPreferencesHelper.getDefaultCurrency()!!.code,
                                    abs(left)
                                ),
                                budgetValue = budget.amount,
                                budget = getFormattedMoney(
                                    appPreferencesHelper.getDefaultCurrency()!!.code,
                                    budget.amount
                                ),
                                leftStrRes = leftStrRes,
                                period = context.getString(periodBudget.key.periodNameRes),
                                categories = budget.categories,
                                progress = progress.toFloat(),
                                progressPercent = df.format(
                                    kotlin.math.min(
                                        100.0,
                                        (spent / budget.amount * 100)
                                    )
                                )
                                    .toString(),
                                categoryDescription = categoryDescription,
                                periodDescription = budgetTImeInterval.getDescription(),
                                graphEntries = getBudgetGraph(transactions, budget, budgetTImeInterval),
                                startPeriod = budgetTImeInterval.getStartDate().toDateString(),
                                endPeriod = budgetTImeInterval.getEndDate().toDateString(),
                                spendCategories = getCategoriesStatistic.getExpenseCategoriesStatistic(
                                    transactions,
                                    appPreferencesHelper.getDefaultCurrency()!!,
                                    budgetTImeInterval,
                                    budget.categories
                                ),
                                maxX = max(budget.amount, spent)
                            )
                        )
                    }
                }
            if (period == null)
                return@async budgets + BaseBudget.AddNewBudget
            else
                return@async budgets
        }.await()

    private fun getBudgetTransactions(
        transactions: List<Transaction>,
        budget: BudgetEntry,
        timeIntervalController: TimeIntervalController
    ): List<Transaction> {
        return transactions.filter {
            timeIntervalController.isInInterval(it.date) && budget.categories.map { budget -> budget.id }
                .contains(it.category.id) && it.transactionType == TransactionType.Expense
        }
    }

    private fun getSpent(
        transactions: List<Transaction>,
        budget: BudgetEntry,
        timeIntervalController: TimeIntervalController
    ): Double {
        return getBudgetTransactions(transactions, budget, timeIntervalController).sumOf { it.value }
    }

    private fun getStartDate(now: Date, firstDay: DayOfWeek): Date {
        var nextNow = now.getAsLocalDate()
        if (firstDay != now.getAsLocalDate().dayOfWeek)
            while (nextNow.dayOfWeek != firstDay){
               nextNow = nextNow.minusDays(1)
            }

        return Date(nextNow)
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
                    TimeIntervalController.MonthlyController(now.getAsLocalDate())
                }
                Quarterly -> {
                    TimeIntervalController.QuarterlyController(now.getAsLocalDate())
                }
                Yearly -> {
                    TimeIntervalController.YearlyController(now.getAsLocalDate())
                }
            }
        }

    private fun getBudgetGraph(
        transactions: List<Transaction>,
        budget: BudgetEntry,
        timeIntervalController: TimeIntervalController
    ): List<BudgetGraphEntry> {
        val budgetTransactions = getBudgetTransactions(transactions, budget, timeIntervalController)
        var sum = 0.0
        var index = 0
        val supportTimeIIntervalController =
            if (timeIntervalController is TimeIntervalController.WeeklyController) TimeIntervalController.DailyController(
                timeIntervalController.getStartDate()
            )
            else
                TimeIntervalController.CustomController(timeIntervalController.getGraphTimeInterval()).apply {
                    startIntervalDate = timeIntervalController.getStartDate()
                    endIntervalDate =
                        Date(timeIntervalController.getStartDate().epochMillis + timeIntervalController.getGraphTimeInterval())
                }
        return getZeroEntries(
            budgetTransactions.groupBy {
                index = isInTimeInterval(index, supportTimeIIntervalController, it)
                index
            }.map { timeIntervalTransactions ->
                sum += timeIntervalTransactions.value.sumOf { it.value }
                BudgetGraphEntry(
                    timeIntervalTransactions.key.toFloat(),
                    nf.parse(df.format(sum))!!.toFloat(),
                    getFormattedMoney(
                        appPreferencesHelper.getDefaultCurrency()!!.code,
                        sum
                    ),
                    Date(timeIntervalController.getStartDate().epochMillis + timeIntervalTransactions.key * timeIntervalController.getGraphTimeInterval()).toDateString()
                )
            },
            timeIntervalController.getDividersCount(),
            timeIntervalController.getStartDate(),
            timeIntervalController.getGraphTimeInterval()
        )
    }

    private fun getZeroEntries(
        entries: List<BudgetGraphEntry>,
        dividerCount: Int,
        startDate: Date,
        step: Long
    ): List<BudgetGraphEntry> {
            val maxX = entries.maxOfOrNull { it.x } ?: -1f
            val minX = entries.minOfOrNull { it.x } ?: 0f
            val maxY = entries.maxOfOrNull { it.y } ?: 0f
            val finalEntries = List((dividerCount - 1 - maxX).toInt()) {
                BudgetGraphEntry(
                    maxX + it.toFloat() + 1, maxY, getFormattedMoney(
                        appPreferencesHelper.getDefaultCurrency()!!.code,
                        maxY.toDouble()
                    ), Date(startDate.epochMillis + (maxX.toLong() + it.toLong() + 1) * step).toDateString()
                )
            }
            return List(minX.toInt()) {
                BudgetGraphEntry(
                    it.toFloat(), 0f, getFormattedMoney(
                        appPreferencesHelper.getDefaultCurrency()!!.code,
                        0.0
                    ), Date(startDate.epochMillis + it.toLong() * step).toDateString()
                )
            } + entries + finalEntries
        }

    private fun isInTimeInterval(
        index: Int,
        supportTimeIIntervalController: TimeIntervalController,
        transaction: Transaction
    ): Int {
        return when {
            supportTimeIIntervalController.isInInterval(transaction.date) -> index
            else -> {
                supportTimeIIntervalController.moveNext()
                isInTimeInterval(index + 1, supportTimeIIntervalController, transaction)
            }
        }
    }
}