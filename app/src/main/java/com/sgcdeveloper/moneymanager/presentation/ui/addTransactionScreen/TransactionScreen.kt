package com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen

import com.sgcdeveloper.moneymanager.domain.util.TransactionType

enum class TransactionScreen (val transactionType: TransactionType){
    Income(TransactionType.Income), Expense(TransactionType.Expense), Transfer(TransactionType.Transfer)
}