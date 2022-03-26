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
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.util.Date
import com.sgcdeveloper.moneymanager.util.WalletSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.max

class GetBudgetsUseCase @Inject constructor(
    private val moneyManagerRepository: MoneyManagerRepository,
    private val context: Context,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val appPreferencesHelper: AppPreferencesHelper,
    private val getCategoriesStatistic: GetCategoriesStatistic
) {

    suspend operator fun invoke(firstDate: Date = Date(LocalDate.now())): List<BaseBudget> =
        CoroutineScope(Dispatchers.IO).async {
            val budgets: MutableList<BaseBudget> = mutableListOf()
            val transactions = getTransactionsUseCase().sortedBy { it.date.epochMillis }
            val budgetEntries = moneyManagerRepository.getAsyncWBudgets()
            budgetEntries.sortedBy { it.period.ordinal }.groupBy { it.period }.forEach { periodBudget ->
                val budgetTImeInterval = getTimeIntervalCController(periodBudget.value[0].period, firstDate)
                val spents = mutableListOf<Double>()
                periodBudget.value.forEach { budget ->
                    spents.add(getSpent(transactions, budget, budgetTImeInterval))
                }
                budgets.add(
                    BaseBudget.BudgetHeader(
                        context.getString(periodBudget.key.periodNameRes),
                        budgetTImeInterval,
                        context.getString(
                            R.string.total_budget,
                            getFormattedMoney(WalletSingleton.wallet.value!!, spents.sum()),
                            getFormattedMoney(
                                WalletSingleton.wallet.value!!,
                                periodBudget.value.sumOf { it.amount }
                            )
                        ),
                        context.getString(periodBudget.key.fullNameRes),
                        budgetTImeInterval.getDescription()
                    )
                )
                periodBudget.value.forEachIndexed { i, budget ->
                    val spent = spents[i]
                    val progress = kotlin.math.min(1f, (spent / budget.amount).toFloat())
                    val left = budget.amount - getSpent(transactions, budget, budgetTImeInterval)
                    val leftStrRes = if (left >= 0) R.string.remain else R.string.overspent
                    val categoryDescription =
                        if (budget.categories.size == TransactionCategory.ExpenseCategory.getAllItems().size)
                            context.getString(R.string.all_category)
                        else
                            budget.categories.filter { it.id != 0 }.joinToString(separator = ", ") {
                                context.getString(
                                    TransactionCategory.ExpenseCategory.getStringRes(it)
                                )
                            }

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
                                kotlin.math.abs(left)
                            ),
                            budgetValue = budget.amount,
                            budget = getFormattedMoney(
                                WalletSingleton.wallet.value!!,
                                budget.amount
                            ),
                            leftStrRes = leftStrRes,
                            period = context.getString(periodBudget.key.periodNameRes),
                            categories = budget.categories,
                            progress = progress,
                            progressPercent = df.format(kotlin.math.min(100f, (spent / budget.amount * 100).toFloat()))
                                .toString(),
                            categoryDescription = categoryDescription,
                            periodDescription = budgetTImeInterval.getDescription(),
                            graphEntries = getBudgetGraph(transactions, budget, budgetTImeInterval),
                            startPeriod = budgetTImeInterval.getStartDate().toDateString(),
                            endPeriod = budgetTImeInterval.getEndDate().toDateString(),
                            spendCategories = getCategoriesStatistic.getExpenseCategoriesStatistic(
                                transactions,
                                WalletSingleton.wallet.value!!,
                                budgetTImeInterval,
                                budget.categories
                            ),
                            maxX = max(budget.amount, spent)
                        )
                    )
                }
            }

            return@async budgets + BaseBudget.AddNewBudget
        }.await()

    private suspend fun getBudgetTransactions(
        transactions: List<Transaction>,
        budget: BudgetEntry,
        timeIntervalController: TimeIntervalController
    ): List<Transaction> = CoroutineScope(Dispatchers.IO).async {
        return@async transactions.filter {
            timeIntervalController.isInInterval(it.date) && budget.categories.map { budget -> budget.id }
                .contains(it.category.id) && it.transactionType == TransactionType.Expense
        }
    }.await()

    private suspend fun getSpent(
        transactions: List<Transaction>,
        budget: BudgetEntry,
        timeIntervalController: TimeIntervalController
    ): Double = CoroutineScope(Dispatchers.IO).async {
        return@async getBudgetTransactions(transactions, budget, timeIntervalController).sumOf { it.value }
    }.await()

    private suspend fun getStartDate(now: Date, firstDay: DayOfWeek): Date = CoroutineScope(Dispatchers.IO).async {
        val dif = if (firstDay == now.getAsLocalDate().dayOfWeek)
            0
        else
            kotlin.math.abs(now.getAsLocalDate().dayOfWeek.value - firstDay.value + 7)
        return@async Date(now.getAsLocalDate().minusDays(dif.toLong()))
    }.await()

    private suspend fun getTimeIntervalCController(period: BudgetPeriod, now: Date): TimeIntervalController =
        CoroutineScope(Dispatchers.IO).async {
            return@async when (period) {
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
        }.await()

    private suspend fun getBudgetGraph(
        transactions: List<Transaction>,
        budget: BudgetEntry,
        timeIntervalController: TimeIntervalController
    ): List<BudgetGraphEntry> = CoroutineScope(Dispatchers.IO).async {
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
        return@async getZeroEntries(
            budgetTransactions.groupBy {
                index = isInTimeInterval(index, supportTimeIIntervalController, it)
                index
            }.map { timeIntervalTransactions ->
                sum += timeIntervalTransactions.value.sumOf { it.value }
                BudgetGraphEntry(
                    timeIntervalTransactions.key.toFloat(),
                    df.format(sum).toFloat(),
                    getFormattedMoney(
                        WalletSingleton.wallet.value!!,
                        sum
                    ),
                    Date(timeIntervalController.getStartDate().epochMillis + timeIntervalTransactions.key * timeIntervalController.getGraphTimeInterval()).toDateString()
                )
            },
            timeIntervalController.getDividersCount(),
            timeIntervalController.getStartDate(),
            timeIntervalController.getGraphTimeInterval()
        )
    }.await()

    private suspend fun getZeroEntries(
        entries: List<BudgetGraphEntry>,
        dividerCount: Int,
        startDate: Date,
        step: Long
    ): List<BudgetGraphEntry> =
        CoroutineScope(Dispatchers.IO).async {
            val maxX = entries.maxOfOrNull { it.x } ?: -1f
            val minX = entries.minOfOrNull { it.x } ?: 0f
            val maxY = entries.maxOfOrNull { it.y } ?: 0f
            val finalEntries = List((dividerCount - 1 - maxX).toInt()) {
                BudgetGraphEntry(
                    maxX + it.toFloat() + 1, maxY, getFormattedMoney(
                        WalletSingleton.wallet.value!!,
                        maxY.toDouble()
                    ), Date(startDate.epochMillis + (maxX.toLong() + it.toLong() + 1) * step).toDateString()
                )
            }
            return@async List(minX.toInt()) {
                BudgetGraphEntry(
                    it.toFloat(), 0f, getFormattedMoney(
                        WalletSingleton.wallet.value!!,
                        0.0
                    ), Date(startDate.epochMillis + it.toLong() * step).toDateString()
                )
            } + entries + finalEntries
        }.await()

    private suspend fun isInTimeInterval(
        index: Int,
        supportTimeIIntervalController: TimeIntervalController,
        transaction: Transaction
    ): Int = CoroutineScope(Dispatchers.IO).async {
        return@async when {
            supportTimeIIntervalController.isInInterval(transaction.date) -> index
            else -> {
                supportTimeIIntervalController.moveNext()
                isInTimeInterval(index + 1, supportTimeIIntervalController, transaction)
            }
        }
    }.await()
}