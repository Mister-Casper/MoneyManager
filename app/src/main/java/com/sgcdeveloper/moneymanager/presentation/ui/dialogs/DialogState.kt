package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import com.sgcdeveloper.moneymanager.domain.model.Currency
import com.sgcdeveloper.moneymanager.domain.model.RecurringInterval
import com.sgcdeveloper.moneymanager.domain.model.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.model.calendar.DayTransactions
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.util.Date

sealed class DialogState {
    class InformDialog(val information: String) : DialogState()
    class WalletPickerDialog(val wallet: Wallet?) : DialogState()
    class SelectTimeIntervalDialog(val timeIntervalController: TimeIntervalController) : DialogState()
    class AddCurrencyRateDialog(val currency: Currency) : DialogState()
    class DeleteDialog(val massage: String) : DialogState()
    class RecurringDialog(val defaultRecurring: RecurringInterval) : DialogState()
    class AddTransactionCategoryDialog(val category: TransactionCategory, val isExpense: Boolean) : DialogState()
    class DeleteTransactionCategoryDialogState(val transactionCategory: TransactionCategory) : DialogState()
    class SelectCustomTImeIntervalDialog(val startDate: Date, val endDate: Date) : DialogState()
    class SelectWalletsDialog(val allWallets: List<Wallet>, val defaultWallets: List<Wallet>) : DialogState()
    class CalendarTransactionsDialog(val dayTransactions: DayTransactions) : DialogState()

    object StringSelectorDialogState : DialogState()
    object SelectCurrenciesDialogState : DialogState()
    object NoneDialogState : DialogState()
    object DeleteWalletDialog : DialogState()
    object DeleteTransactionDialog : DialogState()
    object DatePickerDialog : DialogState()
    object CategoryPickerDialog : DialogState()
    object SelectCategoriesDialog : DialogState()
}