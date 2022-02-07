package com.sgcdeveloper.moneymanager.presentation.ui.addWallet

import com.sgcdeveloper.moneymanager.domain.model.Currency
import com.sgcdeveloper.moneymanager.domain.model.Wallet

sealed class WalletEvent {
    class SetWallet(val wallet: Wallet) : WalletEvent()
    class ChangeWalletName(val name: String) : WalletEvent()
    class ChangeCurrency(val currency: Currency) : WalletEvent()
    class ChangeMoney(val money: String) : WalletEvent()
    class ChangeColor(val color: Int) : WalletEvent()
    class ChangeIcon(val icon: Int) : WalletEvent()

    object ShowDeleteWalletDialog : WalletEvent()
    object ShowChangeCurrencyDialog : WalletEvent()
    object CloseDialog : WalletEvent()
    object InsertWallet : WalletEvent()
    object DeleteWallet : WalletEvent()
}