package com.sgcdeveloper.moneymanager.presentation.ui.calendar

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.model.calendar.TransactionsCalendar
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionsCalendarUseCase
import com.sgcdeveloper.moneymanager.domain.use_case.WalletsUseCases
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.util.WalletChangerListener
import com.sgcdeveloper.moneymanager.util.WalletSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
open class TransactionsCalendarViewModel @Inject constructor(
    private val app: Application,
    private val walletsUseCases: WalletsUseCases,
    private val appPreferencesHelper: AppPreferencesHelper,
    private val getTransactionsCalendarUseCase: GetTransactionsCalendarUseCase
) : AndroidViewModel(app) {

    val state = mutableStateOf(TransactionsCalendarState())

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
                state.value = state.value.copy(wallet = WalletSingleton.wallet.value!!)
                loadTransactions()
            }
        }
        WalletSingleton.addObserver(object : WalletChangerListener {
            override fun walletChanged(newWallet: Wallet?) {
                if (newWallet != null) {
                    state.value = state.value.copy(wallet = newWallet)
                    loadTransactions()
                }
            }
        })
    }

    fun showWalletPickerDialog() {
        state.value = state.value.copy(dialogState = DialogState.WalletPickerDialog(state.value.wallet))
    }

    fun closeDialog() {
        state.value = state.value.copy(dialogState = DialogState.NoneDialogState)
    }

    fun changeWallet(it: Wallet) {
        state.value = state.value.copy(wallet = it, dialogState = DialogState.NoneDialogState)
        loadTransactions()
    }

    fun moveBack() {
        state.value.timeIntervalController.moveBack()
        state.value = state.value.copy(
            description = state.value.timeIntervalController.getDescription()
        )
        loadTransactions()
    }

    fun moveNext() {
        state.value.timeIntervalController.moveNext()
        state.value = state.value.copy(
            description = state.value.timeIntervalController.getDescription()
        )
        loadTransactions()
    }

    private fun loadTransactions() {
        loadingJob?.cancel()
        loadingJob = viewModelScope.launch {
            state.value = state.value.copy(
                transactionsCalendar = getTransactionsCalendarUseCase(
                    state.value.wallet!!,
                    state.value.timeIntervalController
                )
            )
        }
    }
}

data class TransactionsCalendarState(
    val wallets: List<Wallet> = Collections.emptyList(),
    val wallet: Wallet? = null,
    val dialogState: DialogState = DialogState.NoneDialogState,
    val timeIntervalController: TimeIntervalController = TimeIntervalController.MonthlyController(),
    val transactionsCalendar: TransactionsCalendar = TransactionsCalendar(),
    val description: String = TimeIntervalController.MonthlyController().getDescription()
)