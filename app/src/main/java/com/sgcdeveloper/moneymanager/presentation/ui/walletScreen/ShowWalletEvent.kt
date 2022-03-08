package com.sgcdeveloper.moneymanager.presentation.ui.walletScreen

import com.sgcdeveloper.moneymanager.domain.model.Wallet

sealed class ShowWalletEvent {
    class SetShowWallet(val wallet: Wallet) : ShowWalletEvent()

}