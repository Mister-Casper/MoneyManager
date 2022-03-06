package com.sgcdeveloper.moneymanager.presentation.nav

import com.google.gson.Gson
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.TransactionScreen
import com.sgcdeveloper.moneymanager.util.toSafeJson

sealed class Screen(val route: String) {
    object SignIn : Screen("SignIn")
    object SignUp : Screen("SignUp")
    object Init : Screen("Init")
    object Settings : Screen("Settings")
    object AccountSettings : Screen("AccountSettings")
    object PasswordSettings : Screen("PasswordSettings")
    object MoneyManagerScreen : Screen("Home")
    class AddWallet(wallet: Wallet? = null) : Screen("AddWallet/" + Gson().toSafeJson(wallet))
    class AddTransaction(wallet: Wallet? = null) :
        Screen("AddTransaction/" + Gson().toSafeJson(wallet))

    class EditTransaction(transaction: TransactionEntry? = null) :
        Screen("EditTransaction/" + Gson().toSafeJson(transaction))

    class TimeIntervalTransactions(wallet: Wallet? = null) :
        Screen("TimeIntervalTransactions/" + Gson().toSafeJson(wallet))

    class WeeklyStatisticScreen(wallet: Wallet? = null, transactionType: TransactionType? = null) :
        Screen("WeeklyStatisticScreen/" + Gson().toSafeJson(wallet) + "/" + Gson().toSafeJson(transactionType))

    class TransactionCategoryTransactions(wallet: Wallet? = null, category: TransactionCategory? = null) :
        Screen("TransactionCategoryTransactions/" + Gson().toSafeJson(category) + "/" + Gson().toSafeJson(wallet))

    class TransactionCategoryStatisticScreen(defaultScreen: TransactionScreen? = null) :
        Screen("TransactionCategoryStatisticScreen/" + Gson().toSafeJson(defaultScreen))

    class TransactionCategoryForWalletStatisticScreen(wallet: Wallet? = null) :
        Screen("TransactionCategoryForWalletStatisticScreen/" + Gson().toSafeJson(wallet))

    class WalletScreen(wallet: Wallet?) : Screen("WalletScreen/" + Gson().toSafeJson(wallet))
}