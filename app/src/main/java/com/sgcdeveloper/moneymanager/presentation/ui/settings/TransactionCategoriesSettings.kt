package com.sgcdeveloper.moneymanager.presentation.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.None
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.AddTransactionCategoryDialog
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import org.burnoutcrew.reorderable.*

@Composable
fun TransactionCategoriesSettings(
    navController: NavController,
    transactionCategoriesSettingsViewModel: TransactionCategoriesSettingsViewModel
) {
    val dialogState = remember { transactionCategoriesSettingsViewModel.dialogState }.value
    val context = LocalContext.current
    val state: ReorderableState = rememberReorderState()
    val isShowIncomeCategories = remember { transactionCategoriesSettingsViewModel.isShowIncomeCategories }.value
    val items =
        if (isShowIncomeCategories) transactionCategoriesSettingsViewModel.incomeCategories else transactionCategoriesSettingsViewModel.expenseCategories

    if (dialogState is DialogState.AddTransactionCategoryDialog) {
        AddTransactionCategoryDialog(dialogState.category, dialogState.isExpense, {
            transactionCategoriesSettingsViewModel.insertNewCategory(isShowIncomeCategories, it)
            transactionCategoriesSettingsViewModel.closeDialog()
        }, {
            transactionCategoriesSettingsViewModel.closeDialog()
        })
    }

    Column(Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart)
                    .background(MaterialTheme.colors.surface)
                    .padding(top = 16.dp, bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(32.dp)
                        .padding(start = 12.dp)
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
                )
            }
            Icon(painter = painterResource(id = R.drawable.add_icon),
                contentDescription = "Add new transaction category",
                modifier = Modifier
                    .align(
                        Alignment.CenterEnd
                    )
                    .padding(end = 12.dp)
                    .size(40.dp)
                    .clickable {
                        transactionCategoriesSettingsViewModel.showAddTransactionCategoryDialog(
                            None(context),
                            !isShowIncomeCategories
                        )
                    })
        }
        Row(Modifier.fillMaxWidth()) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isShowIncomeCategories) blue else MaterialTheme.colors.background,
                    contentColor = if (isShowIncomeCategories) white else MaterialTheme.colors.onBackground
                ), modifier = Modifier.weight(1f),
                onClick = { transactionCategoriesSettingsViewModel.isShowIncomeCategories.value = true }
            ) {
                Text(text = stringResource(id = R.string.income))
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (!isShowIncomeCategories) blue else MaterialTheme.colors.background,
                    contentColor = if (!isShowIncomeCategories) white else MaterialTheme.colors.onBackground
                ), modifier = Modifier.weight(1f),
                onClick = { transactionCategoriesSettingsViewModel.isShowIncomeCategories.value = false }
            ) {
                Text(text = stringResource(id = R.string.expense))
            }
        }
        LazyColumn(
            state = state.listState, modifier = Modifier
                .padding(top = 8.dp)
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
                        .padding(4.dp),
                ) {
                    Row(Modifier.align(Alignment.CenterVertically).weight(1f)) {
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
                    Row(Modifier.align(Alignment.CenterVertically)) {
                        if (!item.isDefault) {
                            Icon(
                                painter = painterResource(id = R.drawable.edit_icon),
                                contentDescription = "",
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(start = 4.dp)
                                    .clickable {
                                        transactionCategoriesSettingsViewModel.showAddTransactionCategoryDialog(
                                            item,
                                            !isShowIncomeCategories
                                        )
                                    }
                            )
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.list_icon),
                            contentDescription = "",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(start = 4.dp)
                                .detectReorder(state)
                        )
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