
package com.sgcdeveloper.moneymanager.presentation.ui.addBudget

sealed class AddBudgetEvent {

    class ChangeBudgetName(val name:String) : AddBudgetEvent()

    object CloseDialog : AddBudgetEvent()
    object InsertBudget : AddBudgetEvent()
    object ShowWalletPickerDialog : AddBudgetEvent()
}