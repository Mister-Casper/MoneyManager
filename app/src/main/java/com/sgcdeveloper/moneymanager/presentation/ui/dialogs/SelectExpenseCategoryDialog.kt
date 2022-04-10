package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.AllExpense
import com.sgcdeveloper.moneymanager.domain.model.TransactionCategory
import com.sgcdeveloper.moneymanager.presentation.theme.white

@Composable
fun SelectExpenseCategoryDialog(
    expenseItems: List<TransactionCategory>,
    defaultCategories: List<TransactionCategory>,
    onAdd: (categories: List<TransactionCategory>) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var items = if (defaultCategories.containsAll(expenseItems))
        expenseItems
    else
        defaultCategories
    AlertDialog(
        containerColor = MaterialTheme.colors.background,
        onDismissRequest = onDismiss,
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(32.dp)
                            .clickable { onDismiss() }
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
            CategorySelector(expenseItems, defaultCategories) {
                items = it
            }
        },
        confirmButton = {})
}

@Composable
private fun CategorySelector(
    items: List<TransactionCategory>,
    defaultCategory: List<TransactionCategory>,
    onAdd: (category: List<TransactionCategory>) -> Unit,
) {
    val context = LocalContext.current
    val selectedOption = rememberMutableStateListOf<Int>()
    selectedOption.addAll((defaultCategory.map { it.id.toInt() }))

    Column(Modifier.fillMaxWidth()) {
        LazyColumn {
            items(items.size) {
                val item = items[it]
                Row(
                    Modifier
                        .padding(4.dp)
                        .clickable {
                            if (item.id == AllExpense(context).id) {
                                if (item.id.toInt() in selectedOption) {
                                    selectedOption.clear()
                                } else {
                                    selectedOption.addAll(items.map { it.id.toInt() })
                                }
                            } else {
                                selectedOption.remove(AllExpense(context).id.toInt())
                                if (item.id.toInt() in selectedOption) {
                                    selectedOption.removeAll { it == item.id.toInt() }
                                } else {
                                    selectedOption.add(item.id.toInt())
                                }
                            }
                            onAdd(
                                selectedOption
                                    .toSet()
                                    .toList()
                                    .map { items.find { item -> item.id == it.toLong() } as TransactionCategory }
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
                                painter = rememberImagePainter(ContextCompat.getDrawable(context, item.icon)),
                                contentDescription = "",
                                Modifier
                                    .align(Alignment.Center)
                                    .size(32.dp),
                                tint = white
                            )
                        }
                    }
                    Text(
                        text = item.description,
                        color = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .weight(1f),
                        fontSize = 18.sp
                    )
                    RadioButton(
                        selected = (selectedOption.contains(item.id.toInt())),
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