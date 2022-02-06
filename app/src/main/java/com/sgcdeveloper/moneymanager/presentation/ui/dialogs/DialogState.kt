package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

sealed class DialogState {
    object SelectCurrenciesDialogState : DialogState()
    object NoneDialogState : DialogState()
    object AddWalletDialogState:DialogState()
}