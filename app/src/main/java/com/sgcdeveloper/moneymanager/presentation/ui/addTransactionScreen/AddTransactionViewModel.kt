package com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.data.db.TransactionCategoriesDatabase
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.*
import com.sgcdeveloper.moneymanager.domain.use_case.*
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
    private val appPreferencesHelper: AppPreferencesHelper,
    private val getTransactionCategoriesUseCase: GetTransactionCategoriesUseCase,
    private val transactionCategoriesDatabase: TransactionCategoriesDatabase,
    private val getAvailableRates: GetAvailableRates,
    private val insertRate: InsertRate
) : AndroidViewModel(app) {

    lateinit var wallets: LiveData<List<Wallet>>

    lateinit var incomeItems: List<TransactionCategory>
    lateinit var expenseItems: List<TransactionCategory>

    val currentScreen = mutableStateOf(TransactionScreen.Expense)
    val currentScreenName = mutableStateOf(app.getString(R.string.expense))

    val transactionDate = mutableStateOf(Date(LocalDateTime.now()))
    val transactionAmount = mutableStateOf("")
    val formattedTransactionAmount = mutableStateOf("")
    val transactionDescription = mutableStateOf("")
    val transactionIncomeCategory = mutableStateOf<TransactionCategory>(None(app))
    val transactionExpenseCategory = mutableStateOf<TransactionCategory>(None(app))
    val transactionFromWallet = MutableLiveData<Wallet>()
    val transactionToWallet = MutableLiveData<Wallet>()
    var fromWalletRate by mutableStateOf("")
    var isShowFromWalletRate by mutableStateOf(false)
    var toWalletRate by mutableStateOf("")
    var isShowToWalletRate by mutableStateOf(false)

    val isTransactionCanBeSaved = mutableStateOf(false)
    val back = mutableStateOf(false)
    val backDialog = mutableStateOf(false)

    val dialogState = mutableStateOf<DialogState>(DialogState.NoneDialogState)

    var isTransactionFromWallet = true
    var transactionId = 0L
    var recurringTransactionId = 0L
    val recurringInterval = mutableStateOf<RecurringInterval>(RecurringInterval.None)
    val firstDayOfWeek = appPreferencesHelper.getFirstDayOfWeek()
    var isRecurringMode = true
    var isMustBeRecurring = false

    val isAutoReturn = appPreferencesHelper.getAutoReturn()
    var rates = runBlocking { getAvailableRates.getBaseRatesAsync() }

    fun isDarkTheme() = appPreferencesHelper.getIsDarkTheme()

    init {
        transactionCategoriesDatabase.transactionCategoryDao().getTransactionCategoriesLive()
            .observeForever {
                viewModelScope.launch {
                    incomeItems = getTransactionCategoriesUseCase.getIncomeItems()
                    expenseItems = getTransactionCategoriesUseCase.getExpenseItems()
                }
            }
        viewModelScope.launch {
            wallets = walletsUseCases.getWallets.getUIWallets()
        }
        showScreen(appPreferencesHelper.getStartupTransactionType())
    }

    fun onEvent(addTransactionEvent: AddTransactionEvent) {
        when (addTransactionEvent) {
            is AddTransactionEvent.SetDefaultWRecurringTransaction -> {
                if (transactionId == 0L) {
                    recurringTransactionId = addTransactionEvent.recurringTransaction.id
                    isMustBeRecurring = true
                    val transaction = addTransactionEvent.recurringTransaction.transactionEntry
                    transactionId = transaction.id
                    showScreen(transaction.transactionType)
                    transactionDate.value = transaction.date
                    transactionAmount.value = transaction.value.deleteUselessZero()
                    transactionDescription.value = transaction.description
                    if (transaction.transactionType == TransactionType.Income) {
                        transactionIncomeCategory.value = transaction.category
                    } else if (transaction.transactionType == TransactionType.Expense) {
                        transactionExpenseCategory.value = transaction.category
                    }
                    viewModelScope.launch {
                        transactionFromWallet.value =
                            walletsUseCases.getWallets.getWallet(transaction.fromWalletId)
                        if (transaction.transactionType == TransactionType.Transfer)
                            transactionToWallet.value =
                                walletsUseCases.getWallets.getWallet(transaction.toWalletId)
                        isTransactionCanBeSaved.value = checkIsCanBeSaved()
                        updateShowRates()
                        updateFormattedAMount()
                    }
                }
                recurringInterval.value = addTransactionEvent.recurringTransaction.recurringInterval
            }
            is AddTransactionEvent.SetExistTransaction -> {
                if (transactionId == 0L) {
                    isRecurringMode = false
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
                        transactionFromWallet.value =
                            walletsUseCases.getWallets.getWallet(transaction.fromWalletId)
                        if (transaction.transactionType == TransactionType.Transfer)
                            transactionToWallet.value =
                                walletsUseCases.getWallets.getWallet(transaction.toWalletId)
                        isTransactionCanBeSaved.value = checkIsCanBeSaved()
                        updateShowRates()
                        updateFormattedAMount()
                    }
                }
            }
            is AddTransactionEvent.SetDefaultWallet -> {
                if (transactionFromWallet.value == null && addTransactionEvent.wallet.walletId != 0L) {
                    transactionFromWallet.value = addTransactionEvent.wallet
                    showScreen(appPreferencesHelper.getStartupTransactionType())
                    updateFormattedAMount()
                    updateShowRates()
                }
            }
            is AddTransactionEvent.ChangeAddTransactionScreen -> {
                currentScreen.value = addTransactionEvent.transactionScreen
                updateShowRates()
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
                if (addTransactionEvent.description.length <= MAX_DESCRIPTION_SIZE  && !addTransactionEvent.description.contains("[/{}]".toRegex()))
                    transactionDescription.value = addTransactionEvent.description
            }
            is AddTransactionEvent.ShowTransactionCategoryPickerDialog -> {
                dialogState.value = DialogState.CategoryPickerDialog
            }
            is AddTransactionEvent.ChangeTransactionCategory -> {
                val category = addTransactionEvent.category
                if (!category.isExpense) {
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
                updateShowRates()
            }
            is AddTransactionEvent.InsertTransaction -> {
                FirebaseAnalytics.getInstance(app).logEvent("add_transaction", null)
                val category =
                    when (currentScreen.value) {
                        TransactionScreen.Expense -> transactionExpenseCategory.value
                        TransactionScreen.Income -> transactionIncomeCategory.value
                        else -> None(app)
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
                        category,
                        recurringInterval.value,
                        recurringTransactionId
                    )
                }
            }
            is AddTransactionEvent.ShowDeleteTransactionDialog -> {
                dialogState.value = DialogState.DeleteTransactionDialog
            }
            is AddTransactionEvent.DeleteTransaction -> {
                dialogState.value = DialogState.NoneDialogState
                runBlocking {
                    if (recurringTransactionId != 0L) {
                        walletsUseCases.insertTransaction.deleteRecurringTransaction(
                            recurringTransactionId
                        )
                    } else
                        walletsUseCases.insertTransaction.deleteTransaction(transactionId)
                }
            }
            is AddTransactionEvent.ShowRepeatIntervalDialog -> {
                dialogState.value = DialogState.RecurringDialog(recurringInterval.value)
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

    val defaultCurrency = appPreferencesHelper.getDefaultCurrency()

    private fun updateShowRates() {
        if (transactionToWallet.value?.currency?.name == transactionFromWallet.value?.currency?.name || currentScreen.value != TransactionScreen.Transfer) {
            isShowFromWalletRate = false
            isShowToWalletRate = false
            return
        }
        if (defaultCurrency?.name != transactionToWallet.value?.currency?.name && transactionToWallet.value != null) {
            isShowToWalletRate = true
            toWalletRate =
                app.getString(
                    R.string.rate,
                    defaultCurrency!!.code,
                    rates.find { it.currency.name == transactionToWallet.value?.currency?.name }!!.rate,
                    transactionToWallet.value?.currency?.code
                )
        } else
            isShowToWalletRate = false

        if (defaultCurrency?.name != transactionFromWallet.value?.currency?.name && transactionFromWallet.value != null) {
            isShowFromWalletRate = true
            fromWalletRate =
                app.getString(
                    R.string.rate,
                    defaultCurrency!!.code,
                    rates.find { it.currency.name == transactionFromWallet.value?.currency?.name }!!.rate,
                    transactionFromWallet.value?.currency?.code
                )
        } else
            isShowFromWalletRate = false
        if (transactionToWallet.value == null) {
            isShowFromWalletRate = false
        }
        if (transactionFromWallet.value == null) {
            isShowFromWalletRate = false
        }
    }

    private fun checkIsCanBeSaved(): Boolean {
        if (!transactionAmount.value.isRealDouble())
            return false
        if (!(transactionFromWallet.value != null))
            return false
        if (isMustBeRecurring && recurringInterval.value.recurring == Recurring.None)
            return false
        return when (currentScreen.value) {
            TransactionScreen.Transfer -> {
                (transactionToWallet.value != null && transactionToWallet.value?.walletId != transactionFromWallet.value?.walletId)
            }
            TransactionScreen.Income -> {
                (transactionIncomeCategory.value.id != 0L)
            }
            TransactionScreen.Expense -> {
                (transactionExpenseCategory.value.id != 0L)
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

    fun changeRate(b: Boolean) {
        val rate = if (!b)
            rates.find { it.currency.name == transactionFromWallet.value?.currency?.name }!!
        else
            rates.find { it.currency.name == transactionToWallet.value?.currency?.name }!!

        val currency = if (!b)
            transactionFromWallet.value?.currency
        else
            transactionToWallet.value?.currency

        dialogState.value = DialogState.ChangeRateDialogState(rate.id, rate.rate, defaultCurrency!!, currency!!)
    }

    fun insertCurrency(it: Rate) {
        viewModelScope.launch {
            insertRate(it)
            rates = getAvailableRates.getBaseRatesAsync()
            updateShowRates()
        }
    }


}