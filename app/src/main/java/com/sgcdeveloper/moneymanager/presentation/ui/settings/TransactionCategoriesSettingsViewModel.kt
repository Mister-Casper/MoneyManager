package com.sgcdeveloper.moneymanager.presentation.ui.settings

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sgcdeveloper.moneymanager.data.db.TransactionCategoriesDatabase
import com.sgcdeveloper.moneymanager.domain.model.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
open class TransactionCategoriesSettingsViewModel @Inject constructor(
    private val app: Application,
    private val transactionCategoriesDatabase: TransactionCategoriesDatabase,
    private val getTransactionCategoriesUseCase: GetTransactionCategoriesUseCase
) : AndroidViewModel(app) {

    val incomeCategories:SnapshotStateList<TransactionCategory> = mutableStateListOf()
    val expenseCategories:SnapshotStateList<TransactionCategory> = mutableStateListOf()

    init {
        transactionCategoriesDatabase.transactionCategoryDao().getTransactionCategoriesLive().observeForever {
            viewModelScope.launch {
                incomeCategories.clear()
                incomeCategories.addAll(getTransactionCategoriesUseCase.getIncomeItems())
                expenseCategories.clear()
                expenseCategories.addAll(getTransactionCategoriesUseCase.getExpenseItems())
            }
        }
    }



}