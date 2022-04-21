package com.sgcdeveloper.moneymanager.presentation.ui.settings

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.None
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.AddTransactionCategoryDialog
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DeleteDialog
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DeleteWalletDialog
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.burnoutcrew.reorderable.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionCategoriesSettings(
    navController: NavController,
    transactionCategoriesSettingsViewModel: TransactionCategoriesSettingsViewModel
) {
    val dialogState = remember { transactionCategoriesSettingsViewModel.dialogState }.value
    val context = LocalContext.current
    val state: ReorderableState = rememberReorderState()
    val isShowIncomeCategories = remember { transactionCategoriesSettingsViewModel.isShowIncomeCategories }.value
    val items = remember { transactionCategoriesSettingsViewModel.items }
    val scope = rememberCoroutineScope()
    val isMultiSelection = remember { transactionCategoriesSettingsViewModel.isMultiSelection }.value

    if (dialogState is DialogState.AddTransactionCategoryDialog) {
        AddTransactionCategoryDialog(dialogState.category, dialogState.isExpense, {
            var isNew = false
            runBlocking {
                isNew = transactionCategoriesSettingsViewModel.insertNewCategory(isShowIncomeCategories, it)
            }
            if (transactionCategoriesSettingsViewModel.isAutoReturn) {
                transactionCategoriesSettingsViewModel.closeDialog()
                scope.launch {
                    if (isNew) {
                        delay(250)
                        state.listState.animateScrollToItem(items.size + 1)
                    }
                }
            } else
                Toast.makeText(context, context.getString(R.string.category_added), Toast.LENGTH_LONG).show()
        }, {
            transactionCategoriesSettingsViewModel.closeDialog()
        })
    } else if (dialogState is DialogState.DeleteTransactionCategoryDialogState) {
        DeleteDialog(
            massage = stringResource(
                id = R.string.are_u_sure_delte_category,
                dialogState.transactionCategory.description
            ), onDelete = {
                transactionCategoriesSettingsViewModel.deleteCategory(dialogState.transactionCategory)
                transactionCategoriesSettingsViewModel.closeDialog()
            }) {
            transactionCategoriesSettingsViewModel.closeDialog()
        }
    } else if (dialogState is DialogState.DeleteTransactionDialog) {
        DeleteWalletDialog(
            null,
            {
                transactionCategoriesSettingsViewModel.deleteSelected()
                transactionCategoriesSettingsViewModel.changeMultiSelection(-1)
                transactionCategoriesSettingsViewModel.dialogState.value = DialogState.NoneDialogState
            },
            {
                transactionCategoriesSettingsViewModel.dialogState.value = DialogState.NoneDialogState
            },
            title = stringResource(
                id = R.string.are_u_sure_delete_selected_transaction_categories,
                transactionCategoriesSettingsViewModel.selectedCount.value
            )
        )
    }

    Column(Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart)
                    .background(MaterialTheme.colors.surface)
                    .padding(top = 16.dp, bottom = 16.dp, start = 12.dp, end = 12.dp)
            ) {
                if (!isMultiSelection) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(32.dp)
                            .clickable {
                                transactionCategoriesSettingsViewModel.save()
                                navController.popBackStack()
                            }
                    )
                    Text(
                        text = stringResource(id = R.string.transaction_categories_title),
                        fontSize = 22.sp,
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 12.dp)
                            .weight(1f)
                    )
                    Icon(painter = painterResource(id = R.drawable.add_icon),
                        contentDescription = "Add new transaction category",
                        modifier = Modifier
                            .align(
                                Alignment.CenterVertically
                            )
                            .size(32.dp)
                            .clickable {
                                transactionCategoriesSettingsViewModel.showAddTransactionCategoryDialog(
                                    None(context),
                                    !isShowIncomeCategories
                                )
                            })
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.surface)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.cancel_icon),
                            "",
                            Modifier
                                .align(Alignment.CenterVertically)
                                .size(32.dp)
                                .clickable {
                                    transactionCategoriesSettingsViewModel.changeMultiSelection(-1)
                                }
                        )
                        Text(
                            text = transactionCategoriesSettingsViewModel.selectedCount.value,
                            fontSize = 26.sp,
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            "",
                            Modifier
                                .align(Alignment.CenterVertically)
                                .size(32.dp)
                                .clickable {
                                    transactionCategoriesSettingsViewModel.showDeleteSelectedTransactionsDialog()
                                }
                        )
                        var expanded by remember { mutableStateOf(false) }
                        Box(Modifier.align(Alignment.CenterVertically)) {
                            Icon(
                                painter = painterResource(id = R.drawable.dots_icon),
                                contentDescription = "Show menu",
                                Modifier
                                    .size(32.dp)
                                    .clickable { expanded = true }
                            )
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(onClick = {
                                    expanded = false
                                    transactionCategoriesSettingsViewModel.selectAll()
                                }) {
                                    Text(stringResource(id = R.string.select_all))
                                }
                                DropdownMenuItem(onClick = {
                                    expanded = false
                                    transactionCategoriesSettingsViewModel.clearAll()
                                }) {
                                    Text(stringResource(id = R.string.clear_selection))
                                }
                            }
                        }
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isShowIncomeCategories) blue else MaterialTheme.colors.background,
                    contentColor = if (isShowIncomeCategories) white else MaterialTheme.colors.onBackground
                ), modifier = Modifier.weight(1f),
                onClick = { transactionCategoriesSettingsViewModel.changeCategory() }
            ) {
                Text(text = stringResource(id = R.string.income))
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (!isShowIncomeCategories) blue else MaterialTheme.colors.background,
                    contentColor = if (!isShowIncomeCategories) white else MaterialTheme.colors.onBackground
                ), modifier = Modifier.weight(1f),
                onClick = { transactionCategoriesSettingsViewModel.changeCategory() }
            ) {
                Text(text = stringResource(id = R.string.expense))
            }
        }
        LazyColumn(
            state = state.listState, modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxSize()
                .then(
                    Modifier.reorderable(
                        state,
                        onMove = { from, to ->
                            transactionCategoriesSettingsViewModel.move(
                                from,
                                to
                            )
                        },
                        canDragOver = { true })
                )
        ) {
            items(items, { it.id }) { item ->
                Row(
                    Modifier
                        .draggedItem(state.offsetByKey(item.id))
                        .combinedClickable(
                            onClick = {
                                transactionCategoriesSettingsViewModel.onChangedSelection(item.id)
                            },
                            onLongClick = {
                                transactionCategoriesSettingsViewModel.changeMultiSelection(item.id)
                            },
                        )
                        .padding(4.dp),
                ) {
                    Row(
                        Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f)
                    ) {
                        Card(
                            modifier = Modifier
                                .size(48.dp)
                                .padding(4.dp)
                                .align(Alignment.CenterVertically),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Box(modifier = Modifier.background(Color(item.color))) {
                                Icon(
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
                            text = item.description,
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .align(Alignment.CenterVertically)
                                .weight(1f),
                            fontSize = 18.sp
                        )
                    }
                    var expandedMenu by remember { mutableStateOf(false) }
                    Box(Modifier.align(Alignment.CenterVertically)) {
                        Icon(
                            painter = painterResource(id = R.drawable.dots_icon),
                            contentDescription = "Show menu",
                            Modifier
                                .size(32.dp)
                                .clickable { if (!isMultiSelection) expandedMenu = true }
                        )
                        DropdownMenu(
                            expanded = expandedMenu,
                            onDismissRequest = { expandedMenu = false }
                        ) {
                            DropdownMenuItem(onClick = {
                                expandedMenu = false
                                FirebaseAnalytics.getInstance(context).logEvent("edit_category", null)
                                transactionCategoriesSettingsViewModel.showAddTransactionCategoryDialog(
                                    item,
                                    !isShowIncomeCategories
                                )
                            }) {
                                Text(stringResource(id = R.string.edit_category))
                            }
                            DropdownMenuItem(onClick = {
                                expandedMenu = false
                                FirebaseAnalytics.getInstance(context).logEvent("delete_category", null)
                                transactionCategoriesSettingsViewModel.showDeleteCategoryDialog(item)
                            }) {
                                Text(stringResource(id = R.string.delete_category))
                            }
                        }
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.list_icon),
                        contentDescription = "",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(start = 4.dp)
                            .align(Alignment.CenterVertically)
                            .detectReorder(state)
                    )
                    if (isMultiSelection) {
                        Checkbox(
                            checked = item.isSelection,
                            onCheckedChange = {
                                transactionCategoriesSettingsViewModel.onChangedSelection(item.id)
                            })
                    }
                }
            }
        }
    }

    BackHandler {
        transactionCategoriesSettingsViewModel.save()
        navController.popBackStack()
    }
}