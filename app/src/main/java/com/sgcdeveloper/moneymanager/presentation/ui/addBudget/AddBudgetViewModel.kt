package com.sgcdeveloper.moneymanager.presentation.ui.addBudget

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.use_case.WalletsUseCases
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.util.Date
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
open class AddBudgetViewModel @Inject constructor(
    private val app: Application,
    private val walletsUseCases: WalletsUseCases,
    private val appPreferencesHelper: AppPreferencesHelper
) : AndroidViewModel(app) {


    val transactionDate = mutableStateOf(Date(LocalDateTime.now()))
    val transactionAmount = mutableStateOf("")
    val budgetName = mutableStateOf("")
    val transactionIncomeCategory = mutableStateOf<TransactionCategory>(TransactionCategory.None)
    val transactionExpenseCategory = mutableStateOf<TransactionCategory>(TransactionCategory.None)
    val transactionFromWallet = MutableLiveData<Wallet>()
    val transactionToWallet = MutableLiveData<Wallet>()

    val isBudgetCanBeSaved = mutableStateOf(false)
    val back = mutableStateOf(false)
    val backDialog = mutableStateOf(false)

    val dialogState = mutableStateOf<DialogState>(DialogState.NoneDialogState)

    var isTransactionFromWallet = true
    var transactionId = 0L

    fun isDarkTheme() = appPreferencesHelper.getIsDarkTheme()

    init {
        viewModelScope.launch {

        }

    }

    fun onEvent(addBudgetEvent: AddBudgetEvent) {
        when (addBudgetEvent) {

        }
        isBudgetCanBeSaved.value = checkIsCanBeSaved()
    }


    private fun checkIsCanBeSaved(): Boolean {
        return true
    }

    fun clear() {
        transactionDate.value = Date(LocalDateTime.now())
        transactionAmount.value = ""
        budgetName.value = ""
        transactionExpenseCategory.value = TransactionCategory.None
        transactionIncomeCategory.value = TransactionCategory.None
        transactionFromWallet.value = null
        transactionToWallet.value = null
        isBudgetCanBeSaved.value = false
        dialogState.value = DialogState.NoneDialogState
        isTransactionFromWallet = true
        transactionId = 0L
    }
}