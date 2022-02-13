package com.sgcdeveloper.moneymanager.presentation.ui.transactions

import com.sgcdeveloper.moneymanager.domain.model.Wallet

sealed class TransactionEvent {
    class ChangeWallet(val wallet: Wallet) : TransactionEvent()
    class ChangeWalletById(val walletId: Long) : TransactionEvent()

    object ShowWalletPickerDialog : TransactionEvent()
    object CloseDialog : TransactionEvent()
}