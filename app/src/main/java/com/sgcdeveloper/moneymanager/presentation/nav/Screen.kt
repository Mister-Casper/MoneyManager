package com.sgcdeveloper.moneymanager.presentation.nav

import com.sgcdeveloper.moneymanager.data.db.entry.BudgetEntry
import com.sgcdeveloper.moneymanager.domain.model.*
import com.sgcdeveloper.moneymanager.domain.util.BudgetPeriod
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.TransactionScreen
import com.sgcdeveloper.moneymanager.util.gson
import com.sgcdeveloper.moneymanager.util.toSafeJson

sealed class Screen(val route: String) {
    object SignIn : Screen("SignIn")
    object Calculators : Screen("Calculators")
    object Calculator : Screen("Calculator")
    object TipCalculator: Screen("TipCalculator")
    object DepositCalculator: Screen("DepositCalculatorScreen")
    object SignUp : Screen("SignUp")
    object Init : Screen("Init")
    object Welcome : Screen("Welcome")
    object Settings : Screen("Settings")
    object AccountSettings : Screen("AccountSettings")
    object PasswordSettings : Screen("PasswordSettings")
    object ExchangeRatesScreen : Screen("ExchangeRatesScreen")
    object MoneyManagerScreen : Screen("Home")
    object WalletsManagerScreen : Screen("WalletsManagerScreen")
    object BudgetManagerScreen : Screen("BudgetManagerScreen")
    object RegainAccess : Screen("RegainAccess")
    object SearchTransactionsScreen : Screen("SearchTransactionsScreen")
    object TransactionsCalendarScreen : Screen("TransactionsCalendarScreen.kt")

    class BudgetScreen(budget: BaseBudget.BudgetItem?) : Screen("BudgetScreen/" + gson.toSafeJson(budget))
    class AddWallet(wallet: Wallet? = null) : Screen("AddWallet/" + gson.toSafeJson(wallet))
    class AddTransaction(wallet: Wallet? = null) :
        Screen("AddTransaction/" + gson.toSafeJson(wallet))
    class AddDateTransaction(wallet: Wallet? = null,date: String) :
        Screen("AddTransaction/" + gson.toSafeJson(wallet) + "/" + gson.toSafeJson(date))

    class AddRecurringTransaction(recurringTransaction: RecurringTransaction? = null) :
        Screen("AddRecurringTransaction/" + gson.toSafeJson(recurringTransaction))

    class EditTransaction(transaction: Transaction? = null) :
        Screen("EditTransaction/" + gson.toSafeJson(transaction))

    class TimeIntervalTransactions(wallet: Wallet? = null) :
        Screen("TimeIntervalTransactions/" + gson.toSafeJson(wallet))

    class AddBudgetScreen(budget: BudgetEntry? = null) :
        Screen("AddBudgetScreen/" + gson.toSafeJson(budget))

    class WeeklyStatisticScreen(wallet: Wallet? = null, transactionType: TransactionType? = null) :
        Screen("WeeklyStatisticScreen/" + gson.toSafeJson(wallet) + "/" + gson.toSafeJson(transactionType))

    class TransactionCategoryTransactions(wallet: Wallet? = null, category: TransactionCategory? = null) :
        Screen("TransactionCategoryTransactions/" + gson.toSafeJson(category) + "/" + gson.toSafeJson(wallet))

    class TransactionCategoryStatisticScreen(defaultScreen: TransactionScreen? = null) :
        Screen("TransactionCategoryStatisticScreen/" + gson.toSafeJson(defaultScreen))

    class TransactionCategoryForWalletStatisticScreen(wallet: Wallet? = null) :
        Screen("TransactionCategoryForWalletStatisticScreen/" + gson.toSafeJson(wallet))

    class WalletScreen(wallet: Wallet?) : Screen("WalletScreen/" + gson.toSafeJson(wallet))

    class TimeIntervalBudgetManager(period: BudgetPeriod? = null) :
        Screen("TimeIntervalBudgetManager/" + gson.toSafeJson(period))

    class TransactionCategoriesSettingsScreen(val isIncome: String = "true") :
        Screen("TransactionCategoriesSettings/$isIncome")
}