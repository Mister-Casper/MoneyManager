package com.sgcdeveloper.moneymanager.domain.util

import android.content.Context
import com.sgcdeveloper.moneymanager.R

enum class TransactionType(val stringRes:Int) {
    Income(R.string.income), Expense(R.string.expense), Transfer(R.string.transfer);

    companion object {
        fun getByOrdinal(ordinal: Int): TransactionType {
            return values().find { it.ordinal == ordinal }!!
        }
        fun getByName(name: String,context: Context): TransactionType {
            return values().find { context.getString(it.stringRes) == name }!!
        }
    }
}