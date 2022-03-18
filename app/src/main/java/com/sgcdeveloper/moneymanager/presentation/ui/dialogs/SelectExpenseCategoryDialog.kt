package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import com.sgcdeveloper.moneymanager.presentation.theme.white
import java.util.*

@Composable
fun SelectExpenseCategoryDialog(
    defaultCategories: List<TransactionCategory.ExpenseCategory>? = null,
    onAdd: (categories: List<TransactionCategory.ExpenseCategory>) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var items = defaultCategories!!
    AlertDialog(
        containerColor = MaterialTheme.colors.background,
        onDismissRequest = onDismiss,
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .clickable { onDismiss() }
                    )
                    Text(
                        text = stringResource(id = R.string.select_category),
                        color = MaterialTheme.colors.secondary,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp)
                    )
                }
                Button(onClick = {
                    onAdd(items.sortedBy { it.id })
                    onDismiss()
                }, Modifier.align(Alignment.CenterEnd)) {
                    Text(
                        text = stringResource(id = R.string.save),
                        Modifier.align(Alignment.CenterVertically),
                        color = white
                    )
                }
            }
        },
        text = {
            CategorySelector(defaultCategories) {
                items = it
            }
        },
        confirmButton = {})
}

@Composable
private fun CategorySelector(
    defaultCategory: List<TransactionCategory.ExpenseCategory>? = null,
    onAdd: (category: List<TransactionCategory.ExpenseCategory>) -> Unit,
) {
    val selectedOption = rememberMutableStateListOf<TransactionCategory.ExpenseCategory>()
    selectedOption.addAll((defaultCategory ?: Collections.emptyList()).toMutableSet())
    val items = TransactionCategory.ExpenseCategory.getAllItems().toMutableList()

    Column(Modifier.fillMaxWidth()) {
        LazyColumn {
            items(items.size) {
                val item = items[it]
                Row(
                    Modifier
                        .padding(4.dp)
                        .clickable {
                            if (item == TransactionCategory.ExpenseCategory.AllExpense) {
                                if (item in selectedOption) {
                                    selectedOption.clear()
                                } else {
                                    selectedOption.addAll(items)
                                }
                            } else {
                                selectedOption.remove(TransactionCategory.ExpenseCategory.AllExpense)
                                if (item in selectedOption) {
                                    selectedOption.removeAll { it == item }
                                } else {
                                    selectedOption.add(item)
                                }
                            }
                            onAdd(
                                selectedOption
                                    .toSet()
                                    .toList()
                            )
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier
                            .size(48.dp)
                            .padding(4.dp)
                            .align(Alignment.CenterVertically),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Box(modifier = Modifier.background(Color(item.color))) {
                            androidx.compose.material.Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = "",
                                Modifier
                                    .align(Alignment.Center)
                                    .size(32.dp),
                                tint = white
                            )
                        }
                    }
                    Text(
                        text = stringResource(id = item.description),
                        color = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .weight(1f),
                        fontSize = 18.sp
                    )
                    RadioButton(
                        selected = (item in selectedOption),
                        onClick = null
                    )
                }
            }
        }
    }
}

@Composable
fun <T : Any> rememberMutableStateListOf(vararg elements: T): SnapshotStateList<T> {
    return rememberSaveable(
        saver = Saver(
            save = { it.toHashSet() },
            restore = { it.toHashSet().toMutableStateList() }
        )
    ) {
        elements.toHashSet().toMutableStateList()
    }
}