package com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen

import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import java.time.LocalDate

sealed class AddTransactionEvent {
    class SetDefaultWallet(val wallet: Wallet) : AddTransactionEvent()
    class ChangeAddTransactionScreen(val transactionScreen: TransactionScreen) :
        AddTransactionEvent()

    class ChangeTransactionDate(val date: LocalDate) : AddTransactionEvent()
    class ChangeTransactionAmount(val amount: String) : AddTransactionEvent()
    class ChangeTransactionDescription(val description: String) : AddTransactionEvent()
    class ChangeTransactionCategory(val category: TransactionCategory) : AddTransactionEvent()
    class ChangeTransactionWallet(val wallet: Wallet) : AddTransactionEvent()
    class ShowWalletPickerDialog(val isFrom:Boolean = true) : AddTransactionEvent()

    object ShowChangeDateDialog : AddTransactionEvent()
    object CloseDialog : AddTransactionEvent()
    object ShowTransactionCategoryPickerDialog : AddTransactionEvent()
    object InsertTransaction : AddTransactionEvent()
}