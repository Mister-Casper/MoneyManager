package com.sgcdeveloper.moneymanager.presentation.ui.transactions

import com.sgcdeveloper.moneymanager.domain.model.Wallet

sealed class TransactionEvent {
    class ChangeWallet(val wallet: Wallet) : TransactionEvent()
    class ChangeWalletById(val walletId: Long) : TransactionEvent()
    class ChangeSelectionItemMode(val itemId: Long) : TransactionEvent()

    object ChangeSelectionMode : TransactionEvent()
    object ShowWalletPickerDialog : TransactionEvent()
    object CloseDialog : TransactionEvent()
    object ClearAll : TransactionEvent()
    object SelectAll : TransactionEvent()
    object ShowDeleteSelectedTransactionsDialog : TransactionEvent()
    object ShareSelectedTransactions : TransactionEvent()
    object DeleteSelectedTransactions : TransactionEvent()
}