package com.sgcdeveloper.moneymanager.presentation.ui.addBudget

import com.sgcdeveloper.moneymanager.domain.util.BudgetPeriod
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import java.time.LocalDate

sealed class AddBudgetEvent {

    class ChangeBudgetName(val name: String) : AddBudgetEvent()
    class ChangeBudgetAmount(val amount: String) : AddBudgetEvent()
    class ChangeExpenseCategories(val categories: List<TransactionCategory.ExpenseCategory>) : AddBudgetEvent()
    class ChangeColor(val color: Int) : AddBudgetEvent()
    class ChangeBudgetStartDate(val localDate: LocalDate) : AddBudgetEvent()
    class ChangeBudgetPeriod(val budgetPeriod: BudgetPeriod) : AddBudgetEvent()

    object ShowChangeBudgetPeriod : AddBudgetEvent()
    object ShowChangeDateDialog : AddBudgetEvent()
    object CloseDialog : AddBudgetEvent()
    object InsertBudget : AddBudgetEvent()
    object ShowTransactionCategoryPickerDialog : AddBudgetEvent()
}