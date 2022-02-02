package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

sealed class DialogState {
    class SelectCurrenciesDialogState(val defaultCurrencyCode: String) : DialogState()
    object NoneDialogState : DialogState()
}