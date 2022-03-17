package com.sgcdeveloper.moneymanager.presentation.ui.transactions

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.BaseTransactionItem
import com.sgcdeveloper.moneymanager.domain.model.Wallet
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
open class TransactionsViewModel @Inject constructor(
    private val app: Application,
    private val walletsUseCases: WalletsUseCases,
    private val appPreferencesHelper: AppPreferencesHelper
) : AndroidViewModel(app) {
    val wallet = mutableStateOf(WalletSingleton.wallet.value)

    var wallets: LiveData<List<Wallet>> = walletsUseCases.getWallets.getAllUIWallets()
    var transactionItems = mutableStateOf<List<BaseTransactionItem>>(Collections.emptyList())
    val isEmpty = mutableStateOf(false)

    val dialog = mutableStateOf<DialogState>(DialogState.NoneDialogState)
    private var loadTransactionJob: Job? = null

    init {
        wallets.observeForever {
            val savedWalletId = appPreferencesHelper.getDefaultWalletId()
            if (WalletSingleton.wallet.value == null) {
                val savedWallet = it.find { wallet -> wallet.walletId == savedWalletId }
                if (savedWalletId != -1L && savedWallet != null) {
                    WalletSingleton.setWallet(savedWallet)
                } else if (it.isNotEmpty()) {
                    WalletSingleton.setWallet(it[1])
                }
            } else {
                loadTransactions()
            }
        }
        WalletSingleton.addObserver(object : WalletChangerListener {
            override fun walletChanged(newWallet: Wallet?) {
                if (newWallet != null) {
                    wallet.value = newWallet
                    loadTransactions()
                }
            }
        })
    }

    fun onEvent(transactionEvent: TransactionEvent) {
        when (transactionEvent) {
            is TransactionEvent.ShowWalletPickerDialog -> {
                dialog.value = DialogState.WalletPickerDialog(WalletSingleton.wallet.value)
            }
            is TransactionEvent.CloseDialog -> {
                dialog.value = DialogState.NoneDialogState
            }
            is TransactionEvent.ChangeWallet -> {
                WalletSingleton.setWallet(transactionEvent.wallet)
                appPreferencesHelper.setDefaultWalletId(transactionEvent.wallet.walletId)
            }
            is TransactionEvent.ChangeWalletById -> {
                WalletSingleton.setWallet(wallets.value!!.find { it.walletId == transactionEvent.walletId }!!)
                appPreferencesHelper.setDefaultWalletId(transactionEvent.walletId)
            }
        }
    }

    fun loadTransactions() {
        loadTransactionJob?.cancel()
        loadTransactionJob = viewModelScope.launch {
            transactionItems.value = walletsUseCases.getTransactionItems(WalletSingleton.wallet.value!!)
            isEmpty.value = transactionItems.value.isEmpty()
        }
    }
}