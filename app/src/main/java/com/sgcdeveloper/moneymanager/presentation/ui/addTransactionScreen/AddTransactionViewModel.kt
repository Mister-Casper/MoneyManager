package com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionItems
import com.sgcdeveloper.moneymanager.domain.use_case.WalletsUseCases
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.presentation.ui.init.InitViewModel.Companion.MAX_DESCRIPTION_SIZE
import com.sgcdeveloper.moneymanager.presentation.ui.init.InitViewModel.Companion.MAX_MONEY_LENGTH
import com.sgcdeveloper.moneymanager.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
open class AddTransactionViewModel @Inject constructor(
    private val app: Application,
    private val walletsUseCases: WalletsUseCases,
    private val appPreferencesHelper: AppPreferencesHelper
) : AndroidViewModel(app) {

    lateinit var wallets: LiveData<List<Wallet>>

    val currentScreen = mutableStateOf(TransactionScreen.Expense)
    val currentScreenName = mutableStateOf(app.getString(R.string.expense))

    val transactionDate = mutableStateOf(Date(LocalDateTime.now()))
    val transactionAmount = mutableStateOf("")
    val formattedTransactionAmount = mutableStateOf("")
    val transactionDescription = mutableStateOf("")
    val transactionIncomeCategory = mutableStateOf<TransactionCategory>(TransactionCategory.None)
    val transactionExpenseCategory = mutableStateOf<TransactionCategory>(TransactionCategory.None)
    val transactionFromWallet = MutableLiveData<Wallet>()
    val transactionToWallet = MutableLiveData<Wallet>()

    val isTransactionCanBeSaved = mutableStateOf(false)
    val back = mutableStateOf(false)
    val backDialog = mutableStateOf(false)

    val dialogState = mutableStateOf<DialogState>(DialogState.NoneDialogState)

    var isTransactionFromWallet = true
    var transactionId = 0L

    fun isDarkTheme() = appPreferencesHelper.getIsDarkTheme()

    init {
        viewModelScope.launch {
            wallets = walletsUseCases.getWallets.getUIWallets()
        }
        showScreen(appPreferencesHelper.getStartupTransactionType())
    }

    fun onEvent(addTransactionEvent: AddTransactionEvent) {
        when (addTransactionEvent) {
            is AddTransactionEvent.SetExistTransaction -> {
                if (transactionId == 0L) {
                    val transaction = addTransactionEvent.transaction
                    transactionId = transaction.id
                    showScreen(transaction.transactionType)
                    transactionDate.value = transaction.date
                    transactionAmount.value = transaction.baseCurrencyValue.deleteUselessZero()
                    transactionDescription.value = transaction.description
                    if (transaction.transactionType == TransactionType.Income) {
                        transactionIncomeCategory.value = transaction.category
                    } else if (transaction.transactionType == TransactionType.Expense) {
                        transactionExpenseCategory.value = transaction.category
                    }
                    viewModelScope.launch {
                        transactionFromWallet.value = walletsUseCases.getWallets.getWallet(transaction.fromWalletId)
                        if (transaction.transactionType == TransactionType.Transfer)
                            transactionToWallet.value = walletsUseCases.getWallets.getWallet(transaction.toWalletId)
                        isTransactionCanBeSaved.value = checkIsCanBeSaved()
                        updateFormattedAMount()
                    }
                }
            }
            is AddTransactionEvent.SetDefaultWallet -> {
                if (transactionFromWallet.value == null && addTransactionEvent.wallet.walletId != 0L) {
                    transactionFromWallet.value = addTransactionEvent.wallet
                    showScreen(appPreferencesHelper.getStartupTransactionType())
                    updateFormattedAMount()
                }
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
                if (addTransactionEvent.amount.isWillBeDouble() && addTransactionEvent.amount.length <= MAX_MONEY_LENGTH) {
                    transactionAmount.value = addTransactionEvent.amount
                    updateFormattedAMount()
                }
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
                updateFormattedAMount()
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
                        transactionId,
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
            is AddTransactionEvent.ShowDeleteTransactionDialog -> {
                dialogState.value = DialogState.DeleteTransactionDialog
            }
            is AddTransactionEvent.DeleteTransaction -> {
                dialogState.value = DialogState.NoneDialogState
                runBlocking {
                    walletsUseCases.insertTransaction.deleteTransaction(transactionId)
                }
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

    private fun updateFormattedAMount() {
        if (transactionAmount.value.isEmpty()) {
            formattedTransactionAmount.value = ""
            return
        }
        viewModelScope.launch {
            val wallet = if (transactionFromWallet.value == null)
                WalletSingleton.wallet.value!!
            else
                transactionFromWallet.value!!
            formattedTransactionAmount.value = GetTransactionItems.getFormattedMoney(
                wallet,
                transactionAmount.value.toSafeDouble()
            )
        }
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
                (transactionIncomeCategory.value != TransactionCategory.None)
            }
            TransactionScreen.Expense -> {
                (transactionExpenseCategory.value != TransactionCategory.None)
            }
        }
    }

    private fun showScreen(type: TransactionType) {
        currentScreen.value =
            TransactionScreen.values().find { it.transactionType == type }!!
        currentScreenName.value = when (currentScreen.value) {
            TransactionScreen.Expense ->
                app.getString(R.string.expense)
            TransactionScreen.Income ->
                app.getString(R.string.income)
            else -> app.getString(R.string.transfer)
        }
    }

    fun clear() {
        currentScreenName.value = app.getString(R.string.expense)
        transactionDate.value = Date(LocalDateTime.now())
        transactionAmount.value = ""
        formattedTransactionAmount.value = ""
        transactionDescription.value = ""
        transactionExpenseCategory.value = TransactionCategory.None
        transactionIncomeCategory.value = TransactionCategory.None
        transactionFromWallet.value = null
        transactionToWallet.value = null
        isTransactionCanBeSaved.value = false
        dialogState.value = DialogState.NoneDialogState
        isTransactionFromWallet = true
        transactionId = 0L
    }
}