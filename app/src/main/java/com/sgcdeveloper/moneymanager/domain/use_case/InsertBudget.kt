package com.sgcdeveloper.moneymanager.domain.use_case

import androidx.compose.ui.graphics.toArgb
import com.sgcdeveloper.moneymanager.data.db.entry.BudgetEntry
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.util.BudgetPeriod
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import com.sgcdeveloper.moneymanager.presentation.theme.wallet_color_1
import com.sgcdeveloper.moneymanager.util.Date
import com.sgcdeveloper.moneymanager.util.SyncHelper
import javax.inject.Inject

class InsertBudget @Inject constructor(private val moneyManagerRepository: MoneyManagerRepository,private val syncHelper: SyncHelper) {
    suspend operator fun invoke(
        id:Long,
        budgetName: String,
        amount: String,
        categories: List<TransactionCategory.ExpenseCategory>,
        color: Int = wallet_color_1.toArgb(),
        date: Date,
        period: BudgetPeriod
    ) {
        moneyManagerRepository.insertBudget(
            BudgetEntry(
                id = id,
                budgetName = budgetName,
                amount = amount.toDouble(),
                categories = categories.toList(),
                color = color,
                date = date,
                period = period
            )
        )
        syncHelper.syncServerData()
    }
}