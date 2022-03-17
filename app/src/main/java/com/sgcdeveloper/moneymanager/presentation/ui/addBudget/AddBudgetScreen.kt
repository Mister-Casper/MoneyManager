package com.sgcdeveloper.moneymanager.presentation.ui.addBudget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.theme.gray
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.composables.InputField

@Composable
fun AddBudgetScreen(addBudgetViewModel: AddBudgetViewModel, navController: NavController) {
    val dialog = remember { addBudgetViewModel.dialogState }

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
                        navController.popBackStack()
                        addBudgetViewModel.onEvent(AddBudgetEvent.InsertBudget)
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

            }
        }
    }
}


