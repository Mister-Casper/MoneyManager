package com.sgcdeveloper.moneymanager.domain.model

import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry

sealed class BaseTransactionItem {
    class TransactionHeader(val dayNum: String, val dayName: String, val month: String, val money: String) :
        BaseTransactionItem()

    class TransactionItem(
        val transactionEntry: TransactionEntry,
        val color: Int,
        val icon: Int,
        val description: String,
        val category: String,
        val money: String,
        val moneyColor: Int
    ) : BaseTransactionItem()
}