package com.sgcdeveloper.moneymanager.presentation.ui.statistic

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.PieEntry
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.BaseTransactionItem
import com.sgcdeveloper.moneymanager.domain.model.CategoryStatistic
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.use_case.GetWallets
import com.sgcdeveloper.moneymanager.domain.use_case.WalletsUseCases
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.util.WalletSingleton
import com.sgcdeveloper.moneymanager.util.getExpense
import com.sgcdeveloper.moneymanager.util.getIncome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
open class StatisticViewModel @Inject constructor(
    private val app: Application,
    private val walletsUseCases: WalletsUseCases,
    private val appPreferencesHelper: AppPreferencesHelper
) : AndroidViewModel(app) {
    var wallets: LiveData<List<Wallet>> = walletsUseCases.getWallets()
    var timeInterval = mutableStateOf<TimeIntervalController>(TimeIntervalController.MonthlyController())

    var transactionItems = mutableStateOf<List<BaseTransactionItem>>(Collections.emptyList())
    val isEmpty = mutableStateOf(false)
    val description = mutableStateOf(timeInterval.value.getDescription())
    val income = mutableStateOf("")
    val expense = mutableStateOf("")
    val total = mutableStateOf("")

    val dialog = mutableStateOf<DialogState>(DialogState.NoneDialogState)

    var expenseStruct = mutableStateOf<List<CategoryStatistic>>(Collections.emptyList())
    var expenseEntries = mutableStateOf<List<PieEntry>>(Collections.emptyList())
    val expenseColors = mutableStateOf<List<Int>>(Collections.emptyList())

    var incomeStruct = mutableStateOf<List<CategoryStatistic>>(Collections.emptyList())
    var incomeEntries = mutableStateOf<List<PieEntry>>(Collections.emptyList())
    val incomeColors = mutableStateOf<List<Int>>(Collections.emptyList())

    init {
        wallets.observeForever {
            val savedWallet = appPreferencesHelper.getDefaultWalletId()
            if (WalletSingleton.wallet.value == null) {
                if (savedWallet != -1L) {
                    WalletSingleton.wallet.value = it.find { wallet -> wallet.walletId == savedWallet }!!
                } else if (it.isNotEmpty() && WalletSingleton.wallet == null) {
                    WalletSingleton.wallet.value = it[0]
                }
            }
            loadTransactions()
        }
    }

    fun onEvent(transactionEvent: StatisticEvent) {
        when (transactionEvent) {
            is StatisticEvent.ChangeWalletById -> {
                WalletSingleton.wallet.value = wallets.value!!.find { it.walletId == transactionEvent.walletId }
                loadTransactions()
                appPreferencesHelper.setDefaultWalletId(transactionEvent.walletId)
            }
            is StatisticEvent.ShowWalletPickerDialog -> {
                dialog.value = DialogState.WalletPickerDialog(WalletSingleton.wallet.value)
            }
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
            is StatisticEvent.SetWallet -> {
                WalletSingleton.wallet.value = transactionEvent.wallet
                loadTransactions()
                appPreferencesHelper.setDefaultWalletId(transactionEvent.wallet.walletId)
            }
        }
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            description.value = timeInterval.value.getDescription()
            transactionItems.value =
                walletsUseCases.getTransactionItems.getTimeIntervalTransactions(
                    WalletSingleton.wallet.value!!,
                    timeInterval.value
                )
            isEmpty.value = transactionItems.value.isEmpty()

            val incomeMoney = transactionItems.value.getIncome(WalletSingleton.wallet.value!!)
            val expenseMoney = transactionItems.value.getExpense(WalletSingleton.wallet.value!!)
            val totalMoney = incomeMoney + expenseMoney

            income.value = getFormattedMoney(incomeMoney)
            expense.value = getFormattedMoney(expenseMoney)
            total.value = getFormattedMoney(totalMoney)

            expenseStruct.value =
                walletsUseCases.getCategoriesStatistic.getExpenseStatistic(
                    transactionItems.value.filterIsInstance<BaseTransactionItem.TransactionItem>(),
                    WalletSingleton.wallet.value!!
                )
            expenseColors.value = expenseStruct.value.map { it.color }
            expenseEntries.value = expenseStruct.value.map { it.pieEntry }

            incomeStruct.value =
                walletsUseCases.getCategoriesStatistic.getIncomeStatistic(
                    transactionItems.value.filterIsInstance<BaseTransactionItem.TransactionItem>(),
                    WalletSingleton.wallet.value!!
                )
            incomeColors.value = incomeStruct.value.map { it.color }
            incomeEntries.value = incomeStruct.value.map { it.pieEntry }
        }
    }

    private fun getFormattedMoney(money: Double): String {
        val formatter =
            NumberFormat.getCurrencyInstance(GetWallets.getLocalFromISO(WalletSingleton.wallet.value!!.currency.code)!!)
        return formatter.format(money)
    }

}