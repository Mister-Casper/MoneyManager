package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeInterval

sealed class DialogState {
    class InformDialog(val information: String) : DialogState()
    class WalletPickerDialog(val wallet: Wallet?) : DialogState()
    class SelectTimeIntervalDialog(val timeInterval: TimeInterval) : DialogState()

    object SelectCurrenciesDialogState : DialogState()
    object NoneDialogState : DialogState()
    object DeleteWalletDialog : DialogState()
    object DeleteTransactionDialog : DialogState()
    object DatePickerDialog : DialogState()
    object CategoryPickerDialog : DialogState()
}