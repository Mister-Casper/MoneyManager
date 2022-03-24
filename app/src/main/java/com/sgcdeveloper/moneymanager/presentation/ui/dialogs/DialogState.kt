package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import com.sgcdeveloper.moneymanager.domain.model.Currency
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController

sealed class DialogState {
    class InformDialog(val information: String) : DialogState()
    class WalletPickerDialog(val wallet: Wallet?) : DialogState()
    class SelectTimeIntervalDialog(val timeIntervalController: TimeIntervalController) : DialogState()
    class AddCurrencyRateDialog(val currency: Currency): DialogState()
    class DeleteDialog(val massage:String) : DialogState()

    object StringSelectorDialogState : DialogState()
    object SelectCurrenciesDialogState : DialogState()
    object NoneDialogState : DialogState()
    object DeleteWalletDialog : DialogState()
    object DeleteTransactionDialog : DialogState()
    object DatePickerDialog : DialogState()
    object CategoryPickerDialog : DialogState()
    object SelectExpenseCategoryDialog : DialogState()
}