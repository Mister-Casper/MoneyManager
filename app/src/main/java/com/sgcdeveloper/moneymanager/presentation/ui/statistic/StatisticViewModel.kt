package com.sgcdeveloper.moneymanager.presentation.ui.statistic

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.PieEntry
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.BaseTransactionItem
import com.sgcdeveloper.moneymanager.domain.model.CategoryStatistic
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionItems.Companion.getFormattedMoney
import com.sgcdeveloper.moneymanager.domain.use_case.WalletsUseCases
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.util.WalletChangerListener
import com.sgcdeveloper.moneymanager.util.WalletSingleton
import com.sgcdeveloper.moneymanager.util.getExpense
import com.sgcdeveloper.moneymanager.util.getIncome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
open class StatisticViewModel @Inject constructor(
    private val app: Application,
    private val walletsUseCases: WalletsUseCases,
    private val appPreferencesHelper: AppPreferencesHelper
) : AndroidViewModel(app) {

    val state = mutableStateOf(StatisticState())

    fun isDarkTheme() = appPreferencesHelper.getIsDarkTheme()

    private var loadingJob: Job? = null

    init {
        walletsUseCases.getWallets.getAllUIWallets().observeForever {
            state.value = state.value.copy(
                wallets = it
            )
            val savedWalletId = appPreferencesHelper.getDefaultWalletId()
            if (WalletSingleton.wallet.value == null) {
                val savedWallet = it.find { wallet -> wallet.walletId == savedWalletId }
                if (savedWalletId != -1L && savedWallet != null) {
                    WalletSingleton.setWallet(savedWallet)
                } else if (it.isNotEmpty()) {
                    WalletSingleton.setWallet(it[1])
                }
            } else {
                loadTransactions(WalletSingleton.wallet.value!!)
            }
        }
        WalletSingleton.addObserver(object : WalletChangerListener {
            override fun walletChanged(newWallet: Wallet?) {
                if (newWallet != null)
                    loadTransactions(newWallet)
            }
        })
    }

    fun onEvent(transactionEvent: StatisticEvent) {
        when (transactionEvent) {
            is StatisticEvent.ChangeWalletById -> {
                WalletSingleton.setWallet(state.value.wallets.find { it.walletId == transactionEvent.walletId }!!)
                appPreferencesHelper.setDefaultWalletId(transactionEvent.walletId)
            }
            is StatisticEvent.ShowWalletPickerDialog -> {
                state.value = state.value.copy(
                    dialogState = DialogState.WalletPickerDialog(WalletSingleton.wallet.value)
                )
            }
            is StatisticEvent.ChangeTimeInterval -> {
                state.value = state.value.copy(timeIntervalController = transactionEvent.timeIntervalController)
                loadTransactions(WalletSingleton.wallet.value!!)
            }
            is StatisticEvent.MoveBack -> {
                state.value.timeIntervalController.moveBack()
                loadTransactions(WalletSingleton.wallet.value!!)
            }
            is StatisticEvent.MoveNext -> {
                state.value.timeIntervalController.moveNext()
                loadTransactions(WalletSingleton.wallet.value!!)
            }
            is StatisticEvent.ShowSelectTimeIntervalDialog -> {
                state.value =
                    state.value.copy(dialogState = DialogState.SelectTimeIntervalDialog(state.value.timeIntervalController))
            }
            StatisticEvent.CloseDialog -> {
                state.value = state.value.copy(dialogState = DialogState.NoneDialogState)
            }
            is StatisticEvent.SetWallet -> {
                WalletSingleton.setWallet(transactionEvent.wallet)
                appPreferencesHelper.setDefaultWalletId(transactionEvent.wallet.walletId)
            }
        }
    }

    fun loadTransactions(
        wallet: Wallet
    ) {
        loadingJob?.cancel()
        loadingJob = viewModelScope.launch {
            val transactions = walletsUseCases.getTransactionItems.getTimeIntervalTransactions(
                wallet,
                state.value.timeIntervalController
            )
            val income = transactions.getIncome(wallet)
            val expense = transactions.getExpense(wallet)
            val expenseStruct =
                walletsUseCases.getCategoriesStatistic.getExpenseStatistic(
                    transactions.filterIsInstance<BaseTransactionItem.TransactionItem>(),
                    wallet
                )
            val incomeStruct =
                walletsUseCases.getCategoriesStatistic.getIncomeStatistic(
                    transactions.filterIsInstance<BaseTransactionItem.TransactionItem>(),
                    wallet
                )
            state.value = state.value.copy(
                wallet = wallet,
                description = state.value.timeIntervalController.getDescription(),
                transactions = transactions,
                isEmpty = state.value.transactions.isEmpty(),
                income = getFormattedMoney(wallet, income),
                expense = getFormattedMoney(wallet, expense),
                total = getFormattedMoney(wallet, income + expense),
                expenseStruct = expenseStruct,
                expenseColors = expenseStruct.map { it.color },
                expenseEntries = expenseStruct.map { it.pieEntry },
                incomeStruct = incomeStruct,
                incomeColors = incomeStruct.map { it.color },
                incomeEntries = incomeStruct.map { it.pieEntry }
            )
        }
    }
}

data class StatisticState(
    val dialogState: DialogState = DialogState.NoneDialogState,
    val wallets: List<Wallet> = Collections.emptyList(),
    val wallet: Wallet? = null,
    val timeIntervalController: TimeIntervalController = TimeIntervalController.MonthlyController(),
    val transactions: List<BaseTransactionItem> = Collections.emptyList(),
    val isEmpty: Boolean = false,
    val description: String = timeIntervalController.getDescription(),
    val income: String = "",
    val expense: String = "",
    val total: String = "",
    val expenseStruct: List<CategoryStatistic> = Collections.emptyList(),
    val expenseEntries: List<PieEntry> = Collections.emptyList(),
    val expenseColors: List<Int> = Collections.emptyList(),
    val incomeStruct: List<CategoryStatistic> = Collections.emptyList(),
    val incomeEntries: List<PieEntry> = Collections.emptyList(),
    val incomeColors: List<Int> = Collections.emptyList()
)