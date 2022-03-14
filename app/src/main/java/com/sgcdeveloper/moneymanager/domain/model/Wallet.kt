package com.sgcdeveloper.moneymanager.domain.model

import android.os.Parcelable
import androidx.compose.ui.graphics.toArgb
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.use_case.GetWallets.Companion.getLocalFromISO
import com.sgcdeveloper.moneymanager.presentation.theme.wallet_color_1
import com.sgcdeveloper.moneymanager.presentation.theme.wallet_colors
import kotlinx.android.parcel.Parcelize
import java.text.NumberFormat

@Parcelize
open class Wallet(
    var walletId: Long = 0,
    val isDefault: Boolean = false,
    val name: String = "",
    val money: String = "",
    val formattedMoney: String = "",
    val color: Int = wallet_color_1.toArgb(),
    val icon: Int,
    open val currency: Currency,
    var order: Long = -1
) : Parcelable {
    fun copy(
        walletId: Long = this.walletId,
        isDefault: Boolean = this.isDefault,
        name: String = this.name,
        money: String = this.money,
        formattedMoney: String = this.formattedMoney,
        color: Int = this.color,
        icon: Int = this.icon,
        currency: Currency = this.currency,
        order: Long = this.order
    ) = Wallet(walletId, isDefault, name, money, formattedMoney, color, icon, currency,order)
}

@Parcelize
class AddNewWallet(val defaultCurrency: Currency) :
    Wallet(
        0,
        false,
        "",
        "",
        NumberFormat.getCurrencyInstance(getLocalFromISO(defaultCurrency.code)!!).format(0),
        wallet_colors[0].toArgb(),
        R.drawable.wallet_icon_1,
        defaultCurrency
    )