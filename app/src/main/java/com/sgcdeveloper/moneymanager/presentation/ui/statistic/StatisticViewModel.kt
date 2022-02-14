package com.sgcdeveloper.moneymanager.presentation.ui.statistic

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sgcdeveloper.moneymanager.domain.model.BaseTransactionItem
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.use_case.GetWallets
import com.sgcdeveloper.moneymanager.domain.use_case.WalletsUseCases
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
open class StatisticViewModel @Inject constructor(
    private val app: Application,
    private val walletsUseCases: WalletsUseCases
) : AndroidViewModel(app) {
    var timeInterval = mutableStateOf<TimeIntervalController>(TimeIntervalController.MonthlyController())

    var transactionItems = mutableStateOf<List<BaseTransactionItem>>(Collections.emptyList())
    val defaultWallet = MutableLiveData<Wallet>()
    val isEmpty = mutableStateOf(false)
    val description = mutableStateOf(timeInterval.value.getDescription())
    val income = mutableStateOf("")
    val expense = mutableStateOf("")
    val total = mutableStateOf("")

    val dialog = mutableStateOf<DialogState>(DialogState.NoneDialogState)

    init {
        viewModelScope.launch {
            walletsUseCases.getWallets().observeForever {
                if (it.isNotEmpty() && defaultWallet.value == null) {
                    defaultWallet.value = it[0]
                    loadTransactions()
                }
            }
        }
    }

    fun onEvent(transactionEvent: StatisticEvent) {
        when (transactionEvent) {
            is StatisticEvent.ChangeTimeInterval -> {
                timeInterval.value = transactionEvent.timeIntervalController
                description.value = timeInterval.value.getDescription()
                loadTransactions()
            }
            is StatisticEvent.MoveBack -> {
                timeInterval.value.moveBack()
                loadTransactions()
            }
            is StatisticEvent.MoveNext -> {
                timeInterval.value.moveNext()
                loadTransactions()
            }
            is StatisticEvent.ShowSelectTimeIntervalDialog -> {
                dialog.value = DialogState.SelectTimeIntervalDialog(timeInterval.value)
            }
            StatisticEvent.CloseDialog -> {
                dialog.value = DialogState.NoneDialogState
            }
        }
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            description.value = timeInterval.value.getDescription()
            transactionItems.value =
                walletsUseCases.getTransactionItems.getTimeIntervalTransactions(defaultWallet.value!!, timeInterval.value)
            isEmpty.value = transactionItems.value.isEmpty()
            val incomeMoney = transactionItems.value.filterIsInstance<BaseTransactionItem.TransactionItem>()
                .filter { it.transactionEntry.transactionType == TransactionType.Income }.sumOf { it.moneyValue }
            val expenseMoney = transactionItems.value.filterIsInstance<BaseTransactionItem.TransactionItem>()
                .filter { it.transactionEntry.transactionType == TransactionType.Expense }.sumOf { it.moneyValue } * -1
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