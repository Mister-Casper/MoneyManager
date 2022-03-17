package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
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
    defaultCategories: List<TransactionCategory>? = null,
    onAdd: (categories: List<Int>) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        containerColor = MaterialTheme.colors.background,
        onDismissRequest = onDismiss,
        title = {
            Row(Modifier.fillMaxWidth()) {
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
        },
        text = {
            CategorySelector(defaultCategories) {
                onAdd(it)
            }
        },
        confirmButton = {})
}

@Composable
private fun CategorySelector(
    defaultCategory: List<TransactionCategory>? = null,
    onAdd: (category: List<Int>) -> Unit,
) {
    val selectedOption = rememberMutableStateListOf<Int>()
    selectedOption.addAll((defaultCategory ?: Collections.emptyList()).toMutableSet().map { it.id })
    val items = TransactionCategory.ExpenseCategory.getAllItems().toMutableList()

    Column(Modifier.fillMaxWidth()) {
        LazyColumn {
            items(items.size) {
                val item = items[it]
                Row(
                    Modifier
                        .padding(4.dp)
                        .clickable {
                            if (item.id == TransactionCategory.ExpenseCategory.AllExpense.id) {
                                if (item.id in selectedOption) {
                                    selectedOption.clear()
                                } else {
                                    selectedOption.addAll(items.map { it.id })
                                }
                            } else {
                                selectedOption.remove(TransactionCategory.ExpenseCategory.AllExpense.id)
                                if (item.id in selectedOption) {
                                    selectedOption.removeAll { it == item.id }
                                } else {
                                    selectedOption.add(item.id)
                                }
                            }
                            onAdd(selectedOption.toList())
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
                        selected = (item.id in selectedOption),
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