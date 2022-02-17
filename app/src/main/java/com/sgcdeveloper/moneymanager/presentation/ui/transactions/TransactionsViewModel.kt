package com.sgcdeveloper.moneymanager.presentation.ui.transactions

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.BaseTransactionItem
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.use_case.WalletsUseCases
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.util.WalletSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
open class TransactionsViewModel @Inject constructor(
    private val app: Application,
    private val walletsUseCases: WalletsUseCases,
    private val appPreferencesHelper: AppPreferencesHelper
) : AndroidViewModel(app) {
    var wallets: LiveData<List<Wallet>> = walletsUseCases.getWallets()
    var transactionItems = mutableStateOf<List<BaseTransactionItem>>(Collections.emptyList())
    val isEmpty = mutableStateOf(false)

    val dialog = mutableStateOf<DialogState>(DialogState.NoneDialogState)

    init {
        wallets.observeForever {
            val savedWallet = appPreferencesHelper.getDefaultWalletId()
            if (WalletSingleton.wallet.value == null) {
                if (savedWallet != -1L) {
                    WalletSingleton.wallet.value = it.find { wallet -> wallet.walletId == savedWallet }!!
                    loadTransactions()
                } else if (it.isNotEmpty() && WalletSingleton.wallet == null) {
                    WalletSingleton.wallet.value = it[0]
                    loadTransactions()
                }
            }
        }
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
                WalletSingleton.wallet.value = transactionEvent.wallet
                loadTransactions()
                appPreferencesHelper.setDefaultWalletId(transactionEvent.wallet.walletId)
            }
            is TransactionEvent.ChangeWalletById -> {
                WalletSingleton.wallet.value = wallets.value!!.find { it.walletId == transactionEvent.walletId }
                loadTransactions()
                appPreferencesHelper.setDefaultWalletId(transactionEvent.walletId)
            }
        }
    }

    private fun loadTransactions() {
        walletsUseCases.getTransactionItems(WalletSingleton.wallet.value!!).observeForever {
            isEmpty.value = it.isEmpty()
            transactionItems.value = it
            walletsUseCases.getTransactionItems(WalletSingleton.wallet.value!!).removeObserver { }
        }
    }

}