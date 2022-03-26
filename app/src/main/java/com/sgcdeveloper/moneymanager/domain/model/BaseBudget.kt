package com.sgcdeveloper.moneymanager.domain.model

import androidx.annotation.StringRes
import com.sgcdeveloper.moneymanager.data.db.entry.BudgetEntry
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.util.BudgetPeriod
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory

sealed class BaseBudget {
    class BudgetHeader(
        val periodName: String,
        val timeIntervalController: TimeIntervalController,
        val total: String,
        val header: String,
        val periodDescription:String,
        val period:BudgetPeriod
    ) :
        BaseBudget()

    class BudgetItem(
        val budgetEntry: BudgetEntry,
        val color: Int,
        val budgetName: String,
        val categories: List<TransactionCategory.ExpenseCategory>,
        val spent: String,
        val left: String,
        @StringRes val leftStrRes: Int,
        val budgetValue: Double,
        val budget: String,
        val progressPercent: String,
        val period: String,
        val progress: Float,
        val categoryDescription: String,
        val periodDescription: String,
        val graphEntries: List<BudgetGraphEntry>,
        val startPeriod: String,
        val endPeriod: String,
        val spendCategories: List<CategoryStatistic>,
        val maxX: Double
    ) : BaseBudget()

    object AddNewBudget : BaseBudget()
}