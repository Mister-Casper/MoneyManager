package com.sgcdeveloper.moneymanager.presentation.ui.settings

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.sgcdeveloper.moneymanager.data.db.TransactionCategoriesDatabase
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionCategoryEntry
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.use_case.DeleteTransactionCategoryUseCase
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionCategoriesUseCase
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.burnoutcrew.reorderable.ItemPosition
import org.burnoutcrew.reorderable.move
import javax.inject.Inject


@HiltViewModel
open class TransactionCategoriesSettingsViewModel @Inject constructor(
    private val app: Application,
    private val transactionCategoriesDatabase: TransactionCategoriesDatabase,
    private val getTransactionCategoriesUseCase: GetTransactionCategoriesUseCase,
    private val deleteTransactionCategoryUseCase: DeleteTransactionCategoryUseCase,
    private val appPreferencesHelper: AppPreferencesHelper
) : AndroidViewModel(app) {

    private val incomeCategories: SnapshotStateList<TransactionCategory> = mutableStateListOf()
    private val expenseCategories: SnapshotStateList<TransactionCategory> = mutableStateListOf()
    val dialogState = mutableStateOf<DialogState>(DialogState.NoneDialogState)
    val isMultiSelection = mutableStateOf(false)
    val selectedCount = mutableStateOf("0")
    val isShowIncomeCategories = mutableStateOf(false)
    val isAutoReturn = appPreferencesHelper.getAutoReturn()

    val items: SnapshotStateList<TransactionCategory> = mutableStateListOf()

    init {
        transactionCategoriesDatabase.transactionCategoryDao().getTransactionCategoriesLive().observeForever {
            viewModelScope.launch {
                incomeCategories.clear()
                incomeCategories.addAll(getTransactionCategoriesUseCase.getIncomeItems())
                expenseCategories.clear()
                expenseCategories.addAll(getTransactionCategoriesUseCase.getExpenseItems())
                items.clear()
                items.addAll(if (isShowIncomeCategories.value) incomeCategories else expenseCategories)
            }
        }
    }

    fun showDeleteCategoryDialog(item: TransactionCategory) {
        dialogState.value = DialogState.DeleteTransactionCategoryDialogState(item)
    }

    fun changeCategory() {
        save()
        isMultiSelection.value = false
        isShowIncomeCategories.value = !isShowIncomeCategories.value
        items.clear()
        items.addAll(if (isShowIncomeCategories.value) incomeCategories else expenseCategories)
    }

    fun move(from: ItemPosition, to: ItemPosition) {
        if (isShowIncomeCategories.value) {
            items.move(from.index, to.index)
            incomeCategories.move(from.index, to.index)
        } else {
            items.move(from.index, to.index)
            expenseCategories.move(from.index, to.index)
        }
    }

    fun save() {
        viewModelScope.launch {
            incomeCategories.mapIndexed { ix, category ->
                category.also {
                    category.entry = category.entry.copy(order = ix)
                }
            }
            expenseCategories.mapIndexed { ix, category ->
                category.also {
                    category.entry = category.entry.copy(order = ix)
                }
            }

            transactionCategoriesDatabase.transactionCategoryDao()
                .insertTransactionCategoryEntries((incomeCategories + expenseCategories).map { it.entry })
        }
    }

    fun showAddTransactionCategoryDialog(category: TransactionCategory, isExpense: Boolean) {
        save()
        dialogState.value = DialogState.AddTransactionCategoryDialog(category, isExpense)
    }

    fun closeDialog() {
        dialogState.value = DialogState.NoneDialogState
    }

    fun insertNewCategory(isIncome: Boolean, category: TransactionCategoryEntry): Boolean {
        FirebaseAnalytics.getInstance(app).logEvent("insert_transaction_category", null)
        viewModelScope.launch {
            val order = if (category.id == 0L) {
                if (isIncome)
                    incomeCategories.maxOfOrNull { it.order } ?: 1
                else
                    expenseCategories.maxOfOrNull { it.order } ?: 1
            } else
                category.order
            transactionCategoriesDatabase.transactionCategoryDao()
                .insertTransactionCategory(category.copy(order = order, isDefault = 0))
        }
        return category.id == 0L
    }

    fun deleteCategory(transactionCategory: TransactionCategory) {
        runBlocking {
            deleteTransactionCategoryUseCase(transactionCategory.id)
        }
    }

    fun onChangedSelection(id: Long) {
        updateSelection { if (it.id == id) !it.isSelection else it.isSelection }
    }

    fun changeMultiSelection(id: Long) {
        if (isMultiSelection.value) {
            isMultiSelection.value = false
            updateSelection { false }
        } else {
            isMultiSelection.value = true
            onChangedSelection(id)
        }

        selectedCount.value = items.count { it.isSelection }.toString()
    }

    fun showDeleteSelectedTransactionsDialog() {
        dialogState.value = DialogState.DeleteTransactionDialog
    }

    fun selectAll() {
        updateSelection { true }
    }

    fun clearAll() {
        updateSelection { false }
    }

    private fun updateSelection(condition: (it: TransactionCategory) -> Boolean) {
        val newItems = mutableListOf<TransactionCategory>()
        items.forEach {
            newItems.add(it.copy(isSelection = condition(it)))
        }
        items.clear()
        items.addAll(newItems)

        selectedCount.value = items.count { it.isSelection }.toString()
    }

    fun deleteSelected() {
        viewModelScope.launch {
            items.filter { it.isSelection }.forEach {
                deleteTransactionCategoryUseCase(it.id)
            }
        }
    }

}