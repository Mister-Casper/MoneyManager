package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import com.sgcdeveloper.moneymanager.domain.model.Wallet

sealed class DialogState {
    class DeleteWalletDialog(wallet: Wallet) : DialogState()

    object SelectCurrenciesDialogState : DialogState()
    object NoneDialogState : DialogState()
}