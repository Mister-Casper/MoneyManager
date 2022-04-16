package com.sgcdeveloper.moneymanager.presentation.ui.search

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.BaseTransactionItem
import com.sgcdeveloper.moneymanager.domain.model.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionCategoriesUseCase
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionItems
import com.sgcdeveloper.moneymanager.domain.use_case.GetWallets
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.util.Date
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
open class SearchTransactionsViewModel @Inject constructor(
    private val app: Application,
    private val getTransactionCategoriesUseCase: GetTransactionCategoriesUseCase,
    private val getWallets: GetWallets,
    private val getTransactionItems: GetTransactionItems
) : AndroidViewModel(app) {
    val state = mutableStateOf(SearchTransactionsState())
    lateinit var categories: List<TransactionCategory>
    lateinit var wallets: List<Wallet>

    init {
        viewModelScope.launch {
            categories = getTransactionCategoriesUseCase.getAllItems()
            wallets = getWallets.getWallets()
            find()
        }
    }

    fun updateText(it: String) {
        find(newText = it)
    }

    fun showSelectDateDialog() {
        val controller =
            if (state.value.timeIntervalController is TimeIntervalController.AllController) TimeIntervalController.CustomController(
                1
            )
                .apply {
                    val newDate = startIntervalDate.getAsLocalDateTime()
                        .withDayOfMonth(startIntervalDate.getAsLocalDate().lengthOfMonth())
                    endIntervalDate = Date(newDate)
                }
            else
                state.value.timeIntervalController as TimeIntervalController.CustomController

        state.value = state.value.copy(
            dialogState = DialogState.SelectCustomTImeIntervalDialog(
                controller.startIntervalDate,
                controller.endIntervalDate
            )
        )
    }

    fun showSelectCategoriesDialog() {
        state.value = state.value.copy(
            dialogState = DialogState.SelectCategoriesDialog
        )
    }

    fun showSelectWalletsDialog() {
        state.value = state.value.copy(
            dialogState = DialogState.SelectWalletsDialog(wallets, state.value.wallets)
        )
    }

    fun closeDialog() {
        state.value = state.value.copy(
            dialogState = DialogState.NoneDialogState
        )
    }

    fun updateTime(timeController: TimeIntervalController.CustomController) {
        find(
            newTimeIntervalController = timeController,
            newDateText = timeController.getDescription(),
            isDateActive = true
        )
    }

    fun updateCategories(it: List<TransactionCategory>) {
        val categoryDescription =
            if (it.size == categories.size)
                app.getString(R.string.all_category)
            else {
                it.joinToString(separator = ", ", limit = 3) { it.description }
            }
        find(
            newCategories = it,
            isCategoriesActive = true,
            categoryText = categoryDescription
        )
    }

    fun updateWallets(it: List<Wallet>) {
        val walletsDescription =
            if (it.size == categories.size)
                app.getString(R.string.all_category)
            else {
                it.joinToString(separator = ", ", limit = 3) { it.name }
            }
        find(
            newWallets = it,
            isWalletsActive = true,
            walletText = walletsDescription
        )
    }

    fun clearDate() {
        find(isDateActive = false, newTimeIntervalController = TimeIntervalController.AllController(""))
    }

    fun clearCategories() {
        find(isCategoriesActive = false, newCategories = Collections.emptyList())
    }

    fun clearWallets() {
        find(isWalletsActive = false, newWallets = Collections.emptyList())
    }

    private fun find(
        newText: String = state.value.text,
        newTimeIntervalController: TimeIntervalController = state.value.timeIntervalController,
        newCategories: List<TransactionCategory> = state.value.categories,
        newWallets: List<Wallet> = state.value.wallets,
        newDateText: String = state.value.dateText,
        isDateActive: Boolean = state.value.isDateActive,
        isCategoriesActive: Boolean = state.value.isCategoriesActive,
        categoryText: String = state.value.categoryText,
        isWalletsActive: Boolean = state.value.isWalletsActive,
        walletText: String = state.value.walletText
    ) {
        viewModelScope.launch {
            val transactions = getTransactionItems.findTransactions(
                getWallets.getAllWallet(),
                newText,
                newWallets,
                newTimeIntervalController,
                newCategories
            )

            state.value = state.value.copy(
                text = newText,
                timeIntervalController = newTimeIntervalController,
                categories = newCategories,
                wallets = newWallets,
                dateText = newDateText,
                isDateActive = isDateActive,
                isCategoriesActive = isCategoriesActive,
                categoryText = categoryText,
                isWalletsActive = isWalletsActive,
                walletText = walletText,
                transactions = transactions,
                isExistAny = transactions.isNotEmpty(),
                countTransactions = app.getString(R.string.count_founded_transactions)
            )
        }
    }
}

data class SearchTransactionsState(
    val text: String = "",
    val timeIntervalController: TimeIntervalController = TimeIntervalController.AllController(""),
    val isDateActive: Boolean = false,
    val dateText: String = "",
    val isCategoriesActive: Boolean = false,
    val categories: List<TransactionCategory> = Collections.emptyList(),
    val categoryText: String = "",
    val isWalletsActive: Boolean = false,
    val wallets: List<Wallet> = Collections.emptyList(),
    val walletText: String = "",
    val transactions: List<BaseTransactionItem.TransactionItem> = Collections.emptyList(),
    val isExistAny: Boolean = true,
    val countTransactions: String = "",
    val dialogState: DialogState = DialogState.NoneDialogState
)