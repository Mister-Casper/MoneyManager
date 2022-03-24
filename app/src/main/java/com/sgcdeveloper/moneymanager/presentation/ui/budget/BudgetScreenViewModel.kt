package com.sgcdeveloper.moneymanager.presentation.ui.budget

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.BaseBudget
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BudgetScreenViewModel @Inject constructor(
    private val app: Application,
    private val moneyManagerRepository: MoneyManagerRepository
) : AndroidViewModel(app) {

    val dialogState = mutableStateOf<DialogState>(DialogState.NoneDialogState)

    fun showDeleteBudgetDialog() {
        dialogState.value = DialogState.DeleteDialog(app.getString(R.string.delete_dialog_massage))
    }

    fun closeDialog() {
        dialogState.value = DialogState.NoneDialogState
    }

    fun deleteBudget(budget: BaseBudget.BudgetItem) {
        viewModelScope.launch {
            moneyManagerRepository.removeBudget(budget.budgetEntry.id)
        }
    }
}