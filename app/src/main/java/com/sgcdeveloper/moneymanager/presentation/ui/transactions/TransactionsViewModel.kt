package com.sgcdeveloper.moneymanager.presentation.ui.transactions

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
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

    var state = mutableStateOf(TransactionsState())

    private var loadTransactionJob: Job? = null

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
                if (newWallet != null) {
                    loadTransactions(newWallet)
                }
            }
        })
    }

    fun onEvent(transactionEvent: TransactionEvent) {
        when (transactionEvent) {
            is TransactionEvent.ShowWalletPickerDialog -> {
                state.value = state.value.copy(
                    dialogState = DialogState.WalletPickerDialog(WalletSingleton.wallet.value)
                )
            }
            is TransactionEvent.CloseDialog -> {
                state.value = state.value.copy(
                    dialogState = DialogState.NoneDialogState
                )
            }
            is TransactionEvent.ChangeWallet -> {
                WalletSingleton.setWallet(transactionEvent.wallet)
                appPreferencesHelper.setDefaultWalletId(transactionEvent.wallet.walletId)
            }
            is TransactionEvent.ChangeWalletById -> {
                WalletSingleton.setWallet(state.value.wallets.find { it.walletId == transactionEvent.walletId }!!)
                appPreferencesHelper.setDefaultWalletId(transactionEvent.walletId)
            }
            is TransactionEvent.ChangeSelectionMode -> {
                state.value = state.value.copy(
                    isMultiSelectionMode = !state.value.isMultiSelectionMode
                )
                if (!state.value.isMultiSelectionMode) {
                    state.value = state.value.copy(
                        transactions = state.value.transactions.map {
                            if (it is BaseTransactionItem.TransactionItem) {
                                it.copy(
                                    isSelection = false
                                )
                            } else it
                        },
                    )
                }
            }
            is TransactionEvent.ChangeSelectionItemMode -> {
                val newItems = mutableListOf<BaseTransactionItem>()
                state.value.transactions.forEach {
                    if (it is BaseTransactionItem.TransactionItem) {
                        if (transactionEvent.itemId == it.transactionEntry.id) {
                            newItems.add(it.copy(isSelection = !it.isSelection))
                        } else
                            newItems.add(it)
                    } else
                        newItems.add(it)
                }
                state.value = state.value.copy(
                    transactions = newItems,
                    selectedCount = newItems.filterIsInstance<BaseTransactionItem.TransactionItem>().count { it.isSelection }.toString()
                )
            }
        }
    }

    fun loadTransactions(newWallet: Wallet) {
        loadTransactionJob?.cancel()
        loadTransactionJob = viewModelScope.launch {
            val transactions = walletsUseCases.getTransactionItems(newWallet)
            state.value = state.value.copy(
                wallet = newWallet,
                transactions = transactions,
                isEmpty = transactions.isEmpty()
            )
        }
    }
}

data class TransactionsState(
    val wallets: List<Wallet> = Collections.emptyList(),
    val wallet: Wallet? = null,
    val transactions: List<BaseTransactionItem> = Collections.emptyList(),
    val isEmpty: Boolean = false,
    val dialogState: DialogState = DialogState.NoneDialogState,
    val isMultiSelectionMode: Boolean = false,
    val selectedCount: String = "0"
)