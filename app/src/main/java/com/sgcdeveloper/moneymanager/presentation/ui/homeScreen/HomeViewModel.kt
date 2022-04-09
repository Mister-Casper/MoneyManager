package com.sgcdeveloper.moneymanager.presentation.ui.homeScreen

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.BaseBudget
import com.sgcdeveloper.moneymanager.domain.model.BaseRecurringTransaction
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.use_case.GetBudgetsUseCase
import com.sgcdeveloper.moneymanager.domain.use_case.GetRecurringTransactionsUseCase
import com.sgcdeveloper.moneymanager.domain.use_case.WalletsUseCases
import com.sgcdeveloper.moneymanager.util.WalletSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.burnoutcrew.reorderable.ItemPosition
import org.burnoutcrew.reorderable.move
import java.util.*
import javax.inject.Inject

@HiltViewModel
open class HomeViewModel @Inject constructor(
    private val app: Application,
    private val walletsUseCases: WalletsUseCases,
    private val getBudgetsUseCase: GetBudgetsUseCase,
    private val getRecurringTransactionsUseCase: GetRecurringTransactionsUseCase,
    private val appPreferencesHelper: AppPreferencesHelper
) : AndroidViewModel(app) {
    val state = mutableStateOf(HomeState())
    private var loadBudgetsJob: Job? = null

    init {
        walletsUseCases.getWallets.getAllUIWallets().observeForever {
            val savedWalletId = appPreferencesHelper.getDefaultWalletId()
            if (WalletSingleton.wallet.value == null) {
                val savedWallet = it.find { wallet -> wallet.walletId == savedWalletId }
                if (savedWalletId != -1L && savedWallet != null) {
                    WalletSingleton.setWallet(savedWallet)
                } else if (it.isNotEmpty()) {
                    WalletSingleton.setWallet(it[1])
                }
            }
        }
        walletsUseCases.getWallets().observeForever {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    state.value = state.value.copy(
                        wallets = walletsUseCases.getWallets.getUIWalletsOnce(),
                    )
                    state.value.existWallets.clear()
                    state.value.existWallets.addAll(it)
                }
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            state.value.existWallets.mapIndexed { ix, wallet -> wallet.also { wallet.order = ix.toLong() } }
            walletsUseCases.insertWallet.insertWallets(state.value.existWallets)
        }
    }

    fun move(from: ItemPosition, to: ItemPosition) {
        state.value.existWallets.move(from.index, to.index)
    }

    fun deleteWallet(wallet: Wallet) {
        viewModelScope.launch {
            walletsUseCases.deleteWallet(wallet.walletId)
        }
    }

    fun loadBudgets() {
        loadBudgetsJob?.cancel()
        loadBudgetsJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val budgets = getBudgetsUseCase()
                val transactions = getRecurringTransactionsUseCase()
                state.value = state.value.copy(
                    budgets = budgets,
                    recurringTransactions = transactions
                )
            }
        }
    }
}

data class HomeState(
    val wallets: List<Wallet> = Collections.emptyList(),
    val existWallets: SnapshotStateList<Wallet> = mutableStateListOf(),
    val budgets: List<BaseBudget> = Collections.emptyList(),
    val recurringTransactions: List<BaseRecurringTransaction> = Collections.emptyList()
)