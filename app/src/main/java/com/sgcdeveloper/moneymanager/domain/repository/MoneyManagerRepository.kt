package com.sgcdeveloper.moneymanager.domain.repository

import com.sgcdeveloper.moneymanager.data.db.dao.*

interface MoneyManagerRepository : WalletDao, TransactionDao, RateDao, BudgetDao, RecurringTransactionDao,GoalDao{
    suspend fun removeRecurringTransactionsWithCategoryId(categoryId: Long)
}