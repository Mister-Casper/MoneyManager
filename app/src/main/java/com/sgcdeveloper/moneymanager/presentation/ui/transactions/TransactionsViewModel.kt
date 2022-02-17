package com.sgcdeveloper.moneymanager.presentation.ui.transactions

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sgcdeveloper.moneymanager.domain.model.BaseTransactionItem
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.use_case.WalletsUseCases
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
open class TransactionsViewModel @Inject constructor(
    private val app: Application,
    private val walletsUseCases: WalletsUseCases
) : AndroidViewModel(app) {
    var wallets: LiveData<List<Wallet>> = walletsUseCases.getWallets()
    var transactionItems = mutableStateOf<List<BaseTransactionItem>>(Collections.emptyList())
    val defaultWallet = MutableLiveData<Wallet>()
    val isEmpty = mutableStateOf(false)

    val dialog = mutableStateOf<DialogState>(DialogState.NoneDialogState)

    init {
        wallets.observeForever {
            if (it.isNotEmpty() && defaultWallet.value == null) {
                defaultWallet.value = it[0]
                loadTransactions()
            }
        }
    }

    fun onEvent(transactionEvent: TransactionEvent) {
        when (transactionEvent) {
            is TransactionEvent.ShowWalletPickerDialog -> {
                dialog.value = DialogState.WalletPickerDialog(defaultWallet.value)
            }
            is TransactionEvent.CloseDialog -> {
                dialog.value = DialogState.NoneDialogState
            }
            is TransactionEvent.ChangeWallet -> {
                defaultWallet.value = transactionEvent.wallet
                loadTransactions()
            }
            is TransactionEvent.ChangeWalletById -> {
                defaultWallet.value = wallets.value!!.find { it.walletId == transactionEvent.walletId }
                loadTransactions()
            }
        }
    }

    private fun loadTransactions() {
        walletsUseCases.getTransactionItems(defaultWallet.value!!).observeForever {
            isEmpty.value = it.isEmpty()
            transactionItems.value = it
            walletsUseCases.getTransactionItems(defaultWallet.value!!).removeObserver { }
        }
    }

}