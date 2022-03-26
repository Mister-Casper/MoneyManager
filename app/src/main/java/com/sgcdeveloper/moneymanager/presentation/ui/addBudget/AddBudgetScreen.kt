package com.sgcdeveloper.moneymanager.presentation.ui.addBudget

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.util.BudgetPeriod
import com.sgcdeveloper.moneymanager.presentation.nav.BottomMoneyManagerNavigationScreens
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.theme.gray
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.composables.ColorPicker
import com.sgcdeveloper.moneymanager.presentation.ui.composables.InputField
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DatePicker
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.SelectExpenseCategoryDialog
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.StringSelectorDialog

@Composable
fun AddBudgetScreen(addBudgetViewModel: AddBudgetViewModel, navController: NavController) {
    val dialog = remember { addBudgetViewModel.dialogState }
    val context = LocalContext.current

    if (dialog.value is DialogState.SelectExpenseCategoryDialog) {
        SelectExpenseCategoryDialog(addBudgetViewModel.transactionCategories, {
            addBudgetViewModel.onEvent(AddBudgetEvent.ChangeExpenseCategories(it))
        }, {
            addBudgetViewModel.onEvent(AddBudgetEvent.CloseDialog)
        })
    } else if (dialog.value is DialogState.DatePickerDialog) {
        DatePicker(
            defaultDate = addBudgetViewModel.budgetStartDate.value,
            onDateSelected = { addBudgetViewModel.onEvent(AddBudgetEvent.ChangeBudgetStartDate(it)) },
            onDismissRequest = {
                addBudgetViewModel.onEvent(AddBudgetEvent.CloseDialog)
            }
        )
    } else if (dialog.value is DialogState.StringSelectorDialogState) {
        StringSelectorDialog(title = stringResource(id = R.string.budget_period), items = BudgetPeriod.values().map {
            stringResource(
                id = it.periodNameRes
            )
        }, defaultValue = stringResource(id = addBudgetViewModel.budgetPeriod.value.periodNameRes), onSelected = {
            addBudgetViewModel.onEvent(AddBudgetEvent.ChangeBudgetPeriod(BudgetPeriod.values().find { period ->
                context.getString(period.periodNameRes) == it
            }!!))
        }) {
            addBudgetViewModel.onEvent(AddBudgetEvent.CloseDialog)
        }
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        item {
            Row {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(40.dp)
                        .clickable {
                            addBudgetViewModel.clear()
                            navController.popBackStack()
                        }
                )
                Text(
                    text = stringResource(id = R.string.add_budget),
                    color = MaterialTheme.colors.secondary,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp)
                        .weight(1f)
                )
                Button(
                    onClick = {
                        addBudgetViewModel.onEvent(AddBudgetEvent.InsertBudget)
                        addBudgetViewModel.clear()
                        if (!navController.popBackStack(Screen.BudgetManagerScreen.route, false))
                            navController.popBackStack(BottomMoneyManagerNavigationScreens.Home.route, false)
                    }, enabled = addBudgetViewModel.isBudgetCanBeSaved.value,
                    colors = ButtonDefaults.buttonColors(disabledBackgroundColor = gray)
                ) {
                    Text(
                        text = stringResource(id = R.string.save),
                        Modifier.align(Alignment.CenterVertically),
                        color = if (addBudgetViewModel.isBudgetCanBeSaved.value) white else MaterialTheme.colors.secondary
                    )
                }
            }
        }
        item {
            Column(Modifier.fillMaxWidth()) {
                InputField(
                    addBudgetViewModel.budgetName.value,
                    { addBudgetViewModel.onEvent(AddBudgetEvent.ChangeBudgetName(it)) },
                    stringResource(id = R.string.budget_name),
                    false,
                    "",
                )

                TextField(
                    value = addBudgetViewModel.budgetAmount.value,
                    onValueChange = { addBudgetViewModel.onEvent(AddBudgetEvent.ChangeBudgetAmount(it)) },
                    placeholder = { Text(text = "0") },
                    label = { Text(stringResource(id = R.string.amount, addBudgetViewModel.formattedBudgetAmount.value)) },
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                        .align(Alignment.CenterHorizontally),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary)
                )

                val source = remember { MutableInteractionSource() }

                if (source.collectIsPressedAsState().value) {
                    addBudgetViewModel.onEvent(AddBudgetEvent.ShowTransactionCategoryPickerDialog)
                }

                Row(Modifier.fillMaxWidth()) {
                    TextField(
                        value = addBudgetViewModel.getTransactionCategories(LocalContext.current),
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
                        singleLine = true,
                        trailingIcon = {
                            androidx.compose.material.Icon(imageVector = Icons.Filled.KeyboardArrowDown, "")
                        }, placeholder = {
                            Text(text = stringResource(id = R.string.select_category))
                        }, interactionSource = source
                    )
                }

                Text(
                    text = stringResource(id = R.string.budget_color),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp),
                    color = MaterialTheme.colors.secondary
                )
                ColorPicker(40.dp, addBudgetViewModel.colorBudget.value) {
                    addBudgetViewModel.onEvent(AddBudgetEvent.ChangeColor(it))
                }

                val sourceTimeInterval = remember { MutableInteractionSource() }

                if (sourceTimeInterval.collectIsPressedAsState().value) {
                    addBudgetViewModel.onEvent(AddBudgetEvent.ShowChangeBudgetPeriod)
                }

                Row(Modifier.fillMaxWidth()) {
                    TextField(
                        value = stringResource(id = addBudgetViewModel.budgetPeriod.value.periodNameRes),
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
                        singleLine = true,
                        trailingIcon = {
                            androidx.compose.material.Icon(imageVector = Icons.Filled.KeyboardArrowDown, "")
                        }, interactionSource = sourceTimeInterval
                    )
                }
            }
        }
    }

    BackHandler {
        addBudgetViewModel.clear()
        navController.popBackStack()
    }
}