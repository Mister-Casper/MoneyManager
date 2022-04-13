package com.sgcdeveloper.moneymanager.domain.model

import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import java.text.DateFormat

sealed class BaseTransactionItem {
    data class TransactionHeader(val dayNum: String, val dayName: String, val month: String, val money: String) :
        BaseTransactionItem()

    data class TransactionItem(
        val transactionEntry: Transaction,
        val color: Int,
        val icon: Int,
        val description: String,
        val category: String,
        val moneyValue: Double,
        val money: String,
        val moneyColor: Int,
        var isSelection: Boolean = false
    ) : BaseTransactionItem() {
        override fun toString(): String {
            val moneyPrefix = if (transactionEntry.transactionType == TransactionType.Expense)
                " -"
            else
                " "
            return formatter.format(transactionEntry.date.epochMillis) + " " + description + " " + category + moneyPrefix + money
        }

        companion object {
            val formatter: DateFormat = DateFormat.getDateInstance()
        }
    }
}