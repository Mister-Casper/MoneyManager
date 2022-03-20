package com.sgcdeveloper.moneymanager.domain.model

import com.sgcdeveloper.moneymanager.data.db.entry.BudgetEntry
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory

sealed class BaseBudget {
    class BudgetHeader(val periodName:String,val timeIntervalController: TimeIntervalController) :
        BaseBudget()

    class BudgetItem(
        val budgetEntry: BudgetEntry,
        val color: Int,
        val budgetName: String,
        val categories: List<TransactionCategory.ExpenseCategory>,
        val spent: String,
        val left: String,
        val budget: String,
        val period:String,
        val progress:Double
    ) : BaseBudget()

    object AddNewBudget : BaseBudget()
}