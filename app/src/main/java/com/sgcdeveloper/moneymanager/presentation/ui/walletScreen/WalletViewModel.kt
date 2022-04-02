package com.sgcdeveloper.moneymanager.presentation.ui.walletScreen

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.BaseTransactionItem
import com.sgcdeveloper.moneymanager.domain.model.CategoryStatistic
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.use_case.WalletsUseCases
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
open class WalletViewModel @Inject constructor(
    private val app: Application,
    private val walletsUseCases: WalletsUseCases
) : AndroidViewModel(app) {

    lateinit var wallet: Wallet

    val income = mutableStateOf("")
    val expense = mutableStateOf("")
    val transfers = mutableStateOf("")

    var transactionItems = mutableStateOf<List<BaseTransactionItem>>(Collections.emptyList())

    var transactionsStatistic = mutableStateOf<List<CategoryStatistic>>(Collections.emptyList())

    private var loadingJob: Job?= null

    fun onEvent(showWalletEvent: ShowWalletEvent) {
        when (showWalletEvent) {
            is ShowWalletEvent.SetShowWallet -> {
                wallet = showWalletEvent.wallet
                loadingJob?.cancel()
                loadingJob = viewModelScope.launch {
                    transactionItems.value = walletsUseCases.getTransactionItems(wallet)

                    income.value = app.getString(R.string.transactions_count,
                        transactionItems.value.filterIsInstance<BaseTransactionItem.TransactionItem>()
                            .filter { it.transactionEntry.transactionType == TransactionType.Income }.size
                    )
                    expense.value = app.getString(R.string.transactions_count,
                        transactionItems.value.filterIsInstance<BaseTransactionItem.TransactionItem>()
                            .filter { it.transactionEntry.transactionType == TransactionType.Expense }.size
                    )
                    transfers.value = app.getString(R.string.transactions_count,
                        transactionItems.value.filterIsInstance<BaseTransactionItem.TransactionItem>()
                            .filter { it.transactionEntry.transactionType == TransactionType.Transfer }.size
                    )

                    transactionsStatistic.value =
                        walletsUseCases.getCategoriesStatistic.getCategoriesStatistic(
                            wallet.currency,
                            wallet.walletId,
                            transactionItems.value.filterIsInstance<BaseTransactionItem.TransactionItem>().map { it.transactionEntry }
                        )
                }
            }
        }
    }
}