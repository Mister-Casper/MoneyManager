package com.sgcdeveloper.moneymanager.domain.model

import androidx.compose.ui.graphics.toArgb
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.use_case.GetWallets.Companion.getLocalFromISO
import com.sgcdeveloper.moneymanager.presentation.theme.wallet_color_1
import com.sgcdeveloper.moneymanager.presentation.theme.wallet_colors
import java.text.NumberFormat

open class Wallet(
    val walletId: Long = 0,
    val isDefault: Boolean = false,
    val name: String = "",
    val money: String = "",
    val formattedMoney: String = "",
    val color: Int = wallet_color_1.toArgb(),
    val icon: Int,
    val currency: Currency
) {
    fun copy(
        walletId: Long = this.walletId,
        isDefault: Boolean = this.isDefault,
        name: String = this.name,
        money: String = this.money,
        formattedMoney: String = this.formattedMoney,
        color: Int = this.color,
        icon: Int = this.icon,
        currency: Currency = this.currency
    ) = Wallet(walletId, isDefault, name, money, formattedMoney, color, icon, currency)
}

class AddNewWallet(currency: Currency) :
    Wallet(
        0,
        false,
        "",
        "",
        NumberFormat.getCurrencyInstance(getLocalFromISO(currency.code)!!).format(0),
        wallet_colors[0].toArgb(),
        R.drawable.wallet_icon_1,
        currency
    )