package com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.use_case.WalletsUseCases
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.presentation.ui.init.InitViewModel.Companion.MAX_DESCRIPTION_SIZE
import com.sgcdeveloper.moneymanager.presentation.ui.init.InitViewModel.Companion.MAX_MONEY_LENGTH
import com.sgcdeveloper.moneymanager.util.Date
import com.sgcdeveloper.moneymanager.util.isDouble
import com.sgcdeveloper.moneymanager.util.isWillBeDouble
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
open class AddTransactionViewModel @Inject constructor(
    private val app: Application,
    private val walletsUseCases: WalletsUseCases
) : AndroidViewModel(app) {

    lateinit var wallets: LiveData<List<Wallet>>

    val currentScreen = mutableStateOf(TransactionScreen.Expense)
    val currentScreenName = mutableStateOf(app.getString(R.string.expense))

    val transactionDate = mutableStateOf(Date(LocalDateTime.now()))
    val transactionAmount = mutableStateOf("")
    val transactionDescription = mutableStateOf("")
    val transactionIncomeCategory = mutableStateOf<TransactionCategory>(TransactionCategory.None)
    val transactionExpenseCategory = mutableStateOf<TransactionCategory>(TransactionCategory.None)
    val transactionFromWallet = MutableLiveData<Wallet>()
    val transactionToWallet = MutableLiveData<Wallet>()

    val isTransactionCanBeSaved = mutableStateOf(false)

    val dialogState = mutableStateOf<DialogState>(DialogState.NoneDialogState)

    var isTransactionFromWallet = true

    init {
        viewModelScope.launch {
            wallets = walletsUseCases.getWallets()
        }
    }

    fun onEvent(addTransactionEvent: AddTransactionEvent) {
        when (addTransactionEvent) {
            is AddTransactionEvent.SetDefaultWallet -> {
                transactionFromWallet.value = addTransactionEvent.wallet
            }
            is AddTransactionEvent.ChangeAddTransactionScreen -> {
                currentScreen.value = addTransactionEvent.transactionScreen
            }
            is AddTransactionEvent.ShowChangeDateDialog -> {
                dialogState.value = DialogState.DatePickerDialog
            }
            is AddTransactionEvent.CloseDialog -> {
                dialogState.value = DialogState.NoneDialogState
            }
            is AddTransactionEvent.ChangeTransactionDate -> {
                transactionDate.value = Date(addTransactionEvent.date)
            }
            is AddTransactionEvent.ChangeTransactionAmount -> {
                if (addTransactionEvent.amount.isWillBeDouble() && addTransactionEvent.amount.length <= MAX_MONEY_LENGTH)
                    transactionAmount.value = addTransactionEvent.amount
            }
            is AddTransactionEvent.ChangeTransactionDescription -> {
                if (addTransactionEvent.description.length <= MAX_DESCRIPTION_SIZE)
                    transactionDescription.value = addTransactionEvent.description
            }
            is AddTransactionEvent.ShowTransactionCategoryPickerDialog -> {
                dialogState.value = DialogState.CategoryPickerDialog
            }
            is AddTransactionEvent.ChangeTransactionCategory -> {
                val category = addTransactionEvent.category
                if (category is TransactionCategory.IncomeCategory) {
                    currentScreen.value = TransactionScreen.Income
                    transactionIncomeCategory.value = category
                } else {
                    currentScreen.value = TransactionScreen.Expense
                    transactionExpenseCategory.value = category
                }
            }
            is AddTransactionEvent.ShowWalletPickerDialog -> {
                isTransactionFromWallet = addTransactionEvent.isFrom
                val wallet = if (currentScreen.value == TransactionScreen.Transfer) {
                    if (isTransactionFromWallet)
                        transactionFromWallet.value
                    else
                        transactionToWallet.value
                } else
                    transactionFromWallet.value

                dialogState.value = DialogState.WalletPickerDialog(wallet)
            }
            is AddTransactionEvent.ChangeTransactionWallet -> {
                if (currentScreen.value == TransactionScreen.Transfer) {
                    if (isTransactionFromWallet)
                        transactionFromWallet.value = addTransactionEvent.wallet
                    else
                        transactionToWallet.value = addTransactionEvent.wallet
                } else
                    transactionFromWallet.value = addTransactionEvent.wallet
            }
            is AddTransactionEvent.InsertTransaction -> {
                val category =
                    when (currentScreen.value) {
                        TransactionScreen.Expense -> transactionExpenseCategory.value
                        TransactionScreen.Income -> transactionIncomeCategory.value
                        else -> TransactionCategory.None
                    }
                viewModelScope.launch {
                    walletsUseCases.insertTransaction(
                        currentScreen.value.transactionType,
                        transactionFromWallet.value!!,
                        transactionToWallet.value,
                        transactionDescription.value,
                        transactionAmount.value,
                        transactionDate.value,
                        category
                    )
                }
                clear()
            }
        }
        isTransactionCanBeSaved.value = checkIsCanBeSaved()
    }

    fun getDefaultTransactionCategory(): TransactionCategory {
        return if (currentScreen.value == TransactionScreen.Expense)
            transactionExpenseCategory.value
        else
            transactionIncomeCategory.value
    }

    private fun checkIsCanBeSaved(): Boolean {
        if (!transactionAmount.value.isDouble())
            return false
        if (!(transactionFromWallet.value != null))
            return false
        return when (currentScreen.value) {
            TransactionScreen.Transfer -> {
                (transactionToWallet.value != null && transactionToWallet.value?.walletId != transactionFromWallet.value?.walletId)
            }
            TransactionScreen.Income -> {
                (transactionIncomeCategory.value.icon != 0)
            }
            TransactionScreen.Expense -> {
                (transactionExpenseCategory.value.icon != 0)
            }
        }
    }

    private fun clear() {
        currentScreen.value = TransactionScreen.Expense
        currentScreenName.value = app.getString(R.string.expense)
        transactionDate.value = Date(LocalDateTime.now())
        transactionAmount.value = ""
        transactionDescription.value = ""
        transactionExpenseCategory.value = TransactionCategory.None
        transactionIncomeCategory.value = TransactionCategory.None
        transactionFromWallet.value = null
        transactionToWallet.value = null
        isTransactionCanBeSaved.value = false
        dialogState.value = DialogState.NoneDialogState
        isTransactionFromWallet = true
    }
}