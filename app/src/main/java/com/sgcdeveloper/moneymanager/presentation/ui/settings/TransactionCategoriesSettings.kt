package com.sgcdeveloper.moneymanager.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import com.sgcdeveloper.moneymanager.presentation.theme.white

@Composable
fun TransactionCategoriesSettings(navController: NavController,transactionCategoriesSettingsViewModel: TransactionCategoriesSettingsViewModel) {
    val isShowIncomeCategories = remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
                        navController.popBackStack()
                    }
            )
            Text(
                text = stringResource(id = R.string.transaction_categories),
                fontSize = 22.sp,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 12.dp)
            )
        }
        Row(Modifier.fillMaxWidth()) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isShowIncomeCategories.value) blue else MaterialTheme.colors.background,
                    contentColor = if (isShowIncomeCategories.value) white else MaterialTheme.colors.onBackground
                ), modifier = Modifier.weight(1f),
                onClick = { isShowIncomeCategories.value = true }
            ) {
                Text(text = stringResource(id = R.string.income))
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (!isShowIncomeCategories.value) blue else MaterialTheme.colors.background,
                    contentColor = if (!isShowIncomeCategories.value) white else MaterialTheme.colors.onBackground
                ), modifier = Modifier.weight(1f),
                onClick = { isShowIncomeCategories.value = false }
            ) {
                Text(text = stringResource(id = R.string.expense))
            }
        }
        val items =
            if (isShowIncomeCategories.value) transactionCategoriesSettingsViewModel.incomeCategories else transactionCategoriesSettingsViewModel.expenseCategories
        LazyColumn {
            items(items.size) {
                val item = items[it]
                Row(
                    Modifier
                        .padding(4.dp),
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
                            .weight(1f),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }

}