package com.sgcdeveloper.moneymanager.domain.model

import androidx.compose.ui.graphics.toArgb
import com.sgcdeveloper.moneymanager.presentation.theme.blue

open class Wallet(
    val name: String = "",
    val money: String = "0",
    val color: Int = blue.toArgb(),
    val icon: Int,
    val currency: Currency
) {
    fun copy(
        name: String = this.name, money: String = this.money, color: Int = this.color,
        icon: Int = this.icon,
        currency: Currency = this.currency
    ) = Wallet(name, money, color, icon, currency)
}

class AddNewWallet(currency: Currency) :
    Wallet("", "", 0, 0, currency)