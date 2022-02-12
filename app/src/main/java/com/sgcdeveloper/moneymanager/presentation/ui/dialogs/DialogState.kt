package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import com.sgcdeveloper.moneymanager.domain.model.Wallet

sealed class DialogState {
    class InformDialog(val information: String) : DialogState()
    class WalletPickerDialog(val wallet: Wallet?) : DialogState()

    object SelectCurrenciesDialogState : DialogState()
    object NoneDialogState : DialogState()
    object DeleteWalletDialog : DialogState()
    object DeleteTransactionDialog : DialogState()
    object DatePickerDialog : DialogState()
    object CategoryPickerDialog : DialogState()
}