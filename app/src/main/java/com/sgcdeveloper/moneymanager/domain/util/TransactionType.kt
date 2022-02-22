package com.sgcdeveloper.moneymanager.domain.util

enum class TransactionType {
    Income, Expense, Transfer;

    companion object {
        fun getByOrdinal(ordinal: Int): TransactionType {
            return values().find { it.ordinal == ordinal }!!
        }
    }
}