package com.sgcdeveloper.moneymanager.presentation.ui.addBudget

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.repository.CurrencyRepository
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionCategoriesUseCase
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionItems
import com.sgcdeveloper.moneymanager.domain.use_case.InsertBudget
import com.sgcdeveloper.moneymanager.domain.util.BudgetPeriod
import com.sgcdeveloper.moneymanager.presentation.theme.wallet_color_1
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.presentation.ui.init.InitViewModel
import com.sgcdeveloper.moneymanager.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
open class AddBudgetViewModel @Inject constructor(
    private val app: Application,
    private val insertBudget: InsertBudget,
    private val appPreferencesHelper: AppPreferencesHelper,
    private val currencyRepository: CurrencyRepository,
    private val getTransactionCategoriesUseCase: GetTransactionCategoriesUseCase
) : AndroidViewModel(app) {

    val budgetStartDate = mutableStateOf(Date(LocalDateTime.now()))
    val budgetAmount = mutableStateOf("")
    val formattedBudgetAmount = mutableStateOf("")
    val budgetName = mutableStateOf("")
    val defaultCurrency = currencyRepository.getDefaultCurrency()
    val isAutoReturn = appPreferencesHelper.getAutoReturn()
    lateinit var expenseItems:List<TransactionCategory>

    val isBudgetCanBeSaved = mutableStateOf(false)

    val dialogState = mutableStateOf<DialogState>(DialogState.NoneDialogState)
    val transactionCategories = mutableStateListOf<TransactionCategory>()
    val colorBudget = mutableStateOf(wallet_color_1.toArgb())
    val budgetPeriod = mutableStateOf(BudgetPeriod.Weekly)

    var transactionId = 0L

    fun isDarkTheme() = appPreferencesHelper.getIsDarkTheme()

    init {
        viewModelScope.launch {
            expenseItems = getTransactionCategoriesUseCase.getAllExpenseItems()
        }
    }

    fun onEvent(addBudgetEvent: AddBudgetEvent) {
        when (addBudgetEvent) {
            is AddBudgetEvent.SetDefaultBudget -> {
                val budget = addBudgetEvent.budget
                transactionId = budget.id
                budgetAmount.value = budget.amount.deleteUselessZero()
                updateFormattedAMount()
                budgetName.value = budget.budgetName
                transactionCategories.clear()
                transactionCategories.addAll(budget.categories)
                colorBudget.value = budget.color
                budgetPeriod.value = budget.period
            }
            is AddBudgetEvent.ChangeBudgetAmount -> {
                if (addBudgetEvent.amount.isWillBeDouble() && addBudgetEvent.amount.length <= InitViewModel.MAX_MONEY_LENGTH) {
                    budgetAmount.value = addBudgetEvent.amount
                    updateFormattedAMount()
                }
            }
            is AddBudgetEvent.ChangeBudgetName -> {
                if (addBudgetEvent.name.length <= InitViewModel.MAX_DESCRIPTION_SIZE && !addBudgetEvent.name.contains("[/{}]".toRegex()))
                    budgetName.value = addBudgetEvent.name
            }
            is AddBudgetEvent.CloseDialog -> {
                dialogState.value = DialogState.NoneDialogState
            }
            is AddBudgetEvent.InsertBudget -> {
                FirebaseAnalytics.getInstance(app).logEvent("add_budget",null)
                viewModelScope.launch {
                    insertBudget(
                        transactionId,
                        budgetName.value,
                        budgetAmount.value,
                        transactionCategories,
                        colorBudget.value,
                        budgetStartDate.value,
                        budgetPeriod.value
                    )
                }
            }
            is AddBudgetEvent.ShowTransactionCategoryPickerDialog -> {
                dialogState.value = DialogState.SelectCategoriesDialog
            }
            is AddBudgetEvent.ChangeExpenseCategories -> {
                transactionCategories.clear()
                transactionCategories.addAll(addBudgetEvent.categories)
            }
            is AddBudgetEvent.ChangeColor -> {
                colorBudget.value = addBudgetEvent.color
            }
            is AddBudgetEvent.ChangeBudgetStartDate -> {
                budgetStartDate.value = Date(addBudgetEvent.localDate)
            }
            is AddBudgetEvent.ShowChangeDateDialog -> {
                dialogState.value = DialogState.DatePickerDialog
            }
            is AddBudgetEvent.ShowChangeBudgetPeriod -> {
                dialogState.value = DialogState.StringSelectorDialogState
            }
            is AddBudgetEvent.ChangeBudgetPeriod -> {
                budgetPeriod.value = addBudgetEvent.budgetPeriod
            }
        }
        isBudgetCanBeSaved.value = checkIsCanBeSaved()
    }

    private fun updateFormattedAMount() {
        if (budgetAmount.value.isEmpty()) {
            formattedBudgetAmount.value = ""
            return
        }
        viewModelScope.launch {
            val wallet = WalletSingleton.wallet.value!!

            formattedBudgetAmount.value = GetTransactionItems.getFormattedMoney(
                wallet,
                budgetAmount.value.toSafeDouble()
            )
        }
    }

    fun getTransactionCategories(context: Context): String {
        val categories = runBlocking { getTransactionCategoriesUseCase.getAllExpenseItems()}
        if (transactionCategories.size == categories.size) {
            return context.getString(R.string.all_category)
        }
        return transactionCategories.joinToString(separator = ", ") { it.description }
    }

    private fun checkIsCanBeSaved(): Boolean {
        return (budgetAmount.value.isNotEmpty() && transactionCategories.isNotEmpty() && budgetName.value.isNotEmpty())
    }
}