package com.sgcdeveloper.moneymanager.domain.model

sealed class BaseTransactionItem {
    class TransactionHeader(val dayNum: String, val dayName: String, val month: String, val money: String) :
        BaseTransactionItem()

    class TransactionItem(
        val transactionEntry: Transaction,
        val color: Int,
        val icon: Int,
        val description: String,
        val category: String,
        val moneyValue: Double,
        val money: String,
        val moneyColor: Int
    ) : BaseTransactionItem()
}