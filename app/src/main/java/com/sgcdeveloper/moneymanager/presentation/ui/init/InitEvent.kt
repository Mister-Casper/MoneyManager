package com.sgcdeveloper.moneymanager.presentation.ui.init

import com.sgcdeveloper.moneymanager.domain.model.Currency

sealed class InitEvent {
    class ChangeUserName(val newUserName: String) : InitEvent()
    class ChangeCurrency(val currency: Currency) : InitEvent()
    class ChangeDefaultMoney(val newDefaultMoney:String) : InitEvent()
    class ChangeDefaultWalletName(val newDefaultWalletName:String) : InitEvent()

    object ShowChangeCurrencyDialog : InitEvent()
    object CloseDialog : InitEvent()
    object Next : InitEvent()
}