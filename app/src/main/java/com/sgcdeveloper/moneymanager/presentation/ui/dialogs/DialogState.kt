package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

sealed class DialogState {
    class InformDialog(val information: String) : DialogState()

    object SelectCurrenciesDialogState : DialogState()
    object NoneDialogState : DialogState()
    object DeleteWalletDialog : DialogState()
}