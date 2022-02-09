package com.sgcdeveloper.moneymanager.presentation.ui.transactions

import com.sgcdeveloper.moneymanager.domain.model.Wallet

sealed class TransactionEvent {
    class ChangeWallet(val wallet: Wallet) : TransactionEvent()

    object ShowWalletPickerDialog : TransactionEvent()
    object CloseDialog : TransactionEvent()
}