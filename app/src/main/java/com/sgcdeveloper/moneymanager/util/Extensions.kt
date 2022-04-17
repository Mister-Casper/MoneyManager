package com.sgcdeveloper.moneymanager.util

import androidx.compose.ui.graphics.Color
import com.google.gson.Gson
import com.sgcdeveloper.moneymanager.domain.model.BaseTransactionItem
import com.sgcdeveloper.moneymanager.domain.model.Transaction
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.theme.red
import kotlin.math.roundToInt

fun String.isRealDouble(): Boolean {
    val maybeDouble = this.toDoubleOrNull()
    return ((maybeDouble != null))
}

fun String.isDouble(): Boolean {
    val maybeDouble = this.toDoubleOrNull()
    val newline = System.getProperty("line.separator")
    val hasNewline: Boolean = this.contains(newline)
    return ((maybeDouble != null) && !hasNewline) || this.isEmpty()
}

fun String.isWillBeDouble(): Boolean {
    if(!this.isDouble())
        return false
    val a1 = this.split(".").size
    val a2 = this.split(",").size == 1
    val a3 = this.split("-").size == 1
    val a4 = this.split(" ").size == 1
    if (a1 <= 2 && a2 && a3 && a4 && (this.split(".")[0].isNotEmpty() || this.isEmpty())) {
        return true
    }
    return false
}

fun Gson.toSafeJson(src: Any?): String? {
    return if (src == null) {
        ""
    } else toJson(src, src.javaClass)
}

fun String.toSafeDouble(): Double {
    return if (this == "")
        0.0
    else
        this.toDouble()
}

fun Double.deleteUselessZero(): String {
    return if (this.rem(1) == 0.0)
        this.toLong().toString()
    else
        this.toString()
}

fun List<BaseTransactionItem>.getIncome(wallet: Wallet): Double {
    var incomeMoney = this.filterIsInstance<BaseTransactionItem.TransactionItem>()
        .filter { it.transactionEntry.transactionType == TransactionType.Income }.sumOf { it.moneyValue }
    incomeMoney += this.filterIsInstance<BaseTransactionItem.TransactionItem>()
        .filter { it.transactionEntry.transactionType == TransactionType.Transfer }
        .filter { it.transactionEntry.toWalletId == wallet.walletId }.sumOf { it.moneyValue }
    return incomeMoney
}

fun List<BaseTransactionItem>.getExpense(wallet: Wallet): Double {
    var expenseMoney = this.filterIsInstance<BaseTransactionItem.TransactionItem>()
        .filter { it.transactionEntry.transactionType == TransactionType.Expense }.sumOf { it.moneyValue } * -1
    expenseMoney -= this.filterIsInstance<BaseTransactionItem.TransactionItem>()
        .filter { it.transactionEntry.transactionType == TransactionType.Transfer }
        .filter { it.transactionEntry.fromWalletId == wallet.walletId }.sumOf { it.moneyValue }
    return expenseMoney
}

fun Collection<List<Transaction>>.getTransactionsIncome(wallet: Wallet): Double {
    return this.sumOf { it.getTransactionsIncome(wallet) }
}

fun Collection<List<Transaction>>.getTransactionsExpense(wallet: Wallet): Double {
    return this.sumOf { it.getTransactionsExpense(wallet) }
}

fun List<Transaction>.getTransactionsIncome(wallet: Wallet): Double {
    var incomeMoney = this
        .filter { it.transactionType == TransactionType.Income }.sumOf { it.value }
    incomeMoney += this
        .filter { it.transactionType == TransactionType.Transfer }
        .filter { it.toWalletId == wallet.walletId }.sumOf { it.value }
    return incomeMoney
}

fun List<Transaction>.getTransactionsExpense(wallet: Wallet): Double {
    var expenseMoney = this.filter { it.transactionType == TransactionType.Expense }.sumOf { it.value } * -1
    expenseMoney -= this.filter { it.transactionType == TransactionType.Transfer }
        .filter { it.fromWalletId == wallet.walletId }.sumOf { it.value }
    return expenseMoney
}

fun Double.toRoundString(): String {
    return ((this * 100.0).roundToInt() / 100.0).toString()
}

fun Transaction.getMoneyColor(walletId:Long):Color{
    return  if (this.transactionType == TransactionType.Expense) red else if (this.transactionType == TransactionType.Income) Color.Unspecified else {
        if (this.fromWalletId == walletId)
            red
        else
            Color.Unspecified
    }
}

val gson = Gson()