package com.sgcdeveloper.moneymanager.presentation.ui.timeIntervalTransactions

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.BaseTransactionItem
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.use_case.GetWallets
import com.sgcdeveloper.moneymanager.domain.use_case.WalletsUseCases
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.util.getExpense
import com.sgcdeveloper.moneymanager.util.getIncome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
open class TimeIntervalTransactionsViewModel @Inject constructor(
    private val app: Application,
    private val walletsUseCases: WalletsUseCases,
    private val appPreferencesHelper: AppPreferencesHelper
) : AndroidViewModel(app) {
    var timeInterval = mutableStateOf<TimeIntervalController>(TimeIntervalController.DailyController())

    var transactionCategoryFilter = MutableLiveData<TransactionCategory>()
    var transactionItems = mutableStateOf<List<BaseTransactionItem>>(Collections.emptyList())
    val defaultWallet = MutableLiveData<Wallet>()
    val isEmpty = mutableStateOf(false)
    val description = mutableStateOf(timeInterval.value.getDescription())
    val income = mutableStateOf("")
    val expense = mutableStateOf("")
    val total = mutableStateOf("")
    val title = mutableStateOf(app.getString(R.string.transactions))

    val dialog = mutableStateOf<DialogState>(DialogState.NoneDialogState)

    fun isDarkTheme() = appPreferencesHelper.getIsDarkTheme()

    fun onEvent(transactionEvent: TimeIntervalTransactionEvent) {
        when (transactionEvent) {
            is TimeIntervalTransactionEvent.ChangeTimeInterval -> {
                timeInterval.value = transactionEvent.timeIntervalController
            }
            is TimeIntervalTransactionEvent.SetDefaultWalletId -> {
                viewModelScope.launch {
                    defaultWallet.value = walletsUseCases.getWallets.getWallet(transactionEvent.walletId)
                    loadTransactions()
                }
            }
            is TimeIntervalTransactionEvent.SetDefaultWallet -> {
                defaultWallet.value = transactionEvent.wallet
            }
            is TimeIntervalTransactionEvent.MoveBack -> {
                timeInterval.value.moveBack()
                loadTransactions()
            }
            is TimeIntervalTransactionEvent.MoveNext -> {
                timeInterval.value.moveNext()
                loadTransactions()
            }
            is TimeIntervalTransactionEvent.ShowSelectTimeIntervalDialog -> {
                dialog.value = DialogState.SelectTimeIntervalDialog(timeInterval.value)
            }
            is TimeIntervalTransactionEvent.CloseDialog -> {
                dialog.value = DialogState.NoneDialogState
            }
            is TimeIntervalTransactionEvent.ChangeTransactionCategoryFilter -> {
                if (!(transactionEvent.category is TransactionCategory.All)) {
                    transactionCategoryFilter.value = transactionEvent.category
                    title.value = app.getString(transactionEvent.category.description)
                } else {
                    transactionCategoryFilter.value = null
                    title.value = app.getString(R.string.transactions)
                }
                loadTransactions()
            }
        }
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            description.value = timeInterval.value.getDescription()
            transactionItems.value =
                walletsUseCases.getTransactionItems.getTimeIntervalTransactions(
                    defaultWallet.value!!,
                    timeInterval.value,
                    transactionCategoryFilter.value
                )
            isEmpty.value = transactionItems.value.isEmpty()

            val incomeMoney = transactionItems.value.getIncome(defaultWallet.value!!)
            val expenseMoney = transactionItems.value.getExpense(defaultWallet.value!!)
            val totalMoney = incomeMoney + expenseMoney

            income.value = getFormattedMoney(incomeMoney)
            expense.value = getFormattedMoney(expenseMoney)
            total.value = getFormattedMoney(totalMoney)
        }
    }

    private fun getFormattedMoney(money: Double): String {
        val formatter =
            NumberFormat.getCurrencyInstance(GetWallets.getLocalFromISO(defaultWallet.value!!.currency.code)!!)
        return formatter.format(money)
    }

    fun clear() {
        timeInterval.value = TimeIntervalController.DailyController()
        dialog.value = DialogState.NoneDialogState
    }

}