package com.sgcdeveloper.moneymanager.presentation.nav

sealed class Screen(val route: String) {
    object SignIn: Screen("SignIn")
    object SignUp: Screen("SignUp")
    object Init: Screen("Init")
    object MoneyManagerScreen: Screen("Home")
}