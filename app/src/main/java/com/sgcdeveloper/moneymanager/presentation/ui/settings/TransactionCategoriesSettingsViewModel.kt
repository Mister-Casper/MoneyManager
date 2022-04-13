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
import com.sgcdeveloper.moneymanager.domain.model.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.use_case.DeleteTransactionCategoryUseCase
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionCategoriesUseCase
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ItemPosition
import org.burnoutcrew.reorderable.move
import javax.inject.Inject


@HiltViewModel
open class TransactionCategoriesSettingsViewModel @Inject constructor(
    private val app: Application,
    private val transactionCategoriesDatabase: TransactionCategoriesDatabase,
    private val getTransactionCategoriesUseCase: GetTransactionCategoriesUseCase,
    private val deleteTransactionCategoryUseCase: DeleteTransactionCategoryUseCase
) : AndroidViewModel(app) {

    private val incomeCategories: SnapshotStateList<TransactionCategory> = mutableStateListOf()
    private val expenseCategories: SnapshotStateList<TransactionCategory> = mutableStateListOf()
    val dialogState = mutableStateOf<DialogState>(DialogState.NoneDialogState)
    val isShowIncomeCategories = mutableStateOf(false)

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

    fun insertNewCategory(isIncome: Boolean, category: TransactionCategoryEntry) {
        FirebaseAnalytics.getInstance(app).logEvent("insert_transaction_category", null)
        viewModelScope.launch {
            val order = if (category.id == 0L) {
                if (isIncome)
                    incomeCategories.maxOf { it.order }
                else
                    expenseCategories.maxOf { it.order }
            } else
                category.order
            transactionCategoriesDatabase.transactionCategoryDao()
                .insertTransactionCategory(category.copy(order = order, isDefault = 0))
        }
    }

    fun deleteCategory(transactionCategory: TransactionCategory) {
        viewModelScope.launch {
            deleteTransactionCategoryUseCase(transactionCategory.id)
        }
    }


}