package com.sgcdeveloper.moneymanager.presentation.nav

import com.google.gson.Gson
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.util.toSafeJson

sealed class Screen(val route: String) {
    object SignIn : Screen("SignIn")
    object SignUp : Screen("SignUp")
    object Init : Screen("Init")
    object MoneyManagerScreen : Screen("Home")
    class AddWallet(wallet: Wallet? = null) : Screen("AddWallet/" + Gson().toSafeJson(wallet))
    class AddTransaction(wallet: Wallet? = null) : Screen("AddTransaction/" + Gson().toSafeJson(wallet))
}