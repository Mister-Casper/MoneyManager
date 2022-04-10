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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.sgcdeveloper.moneymanager.domain.model.TransactionCategory
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import com.sgcdeveloper.moneymanager.presentation.theme.white

@Composable
fun SelectTransactionCategoryDialog(
    incomeItems: List<TransactionCategory>,
    expenseItems: List<TransactionCategory>,
    isIncome: Boolean,
    defaultCategory: TransactionCategory? = null,
    onAdd: (category: TransactionCategory) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        containerColor = MaterialTheme.colors.background,
        onDismissRequest = onDismiss,
        title = {
            Row(Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(32.dp)
                        .clickable { onDismiss() },
                    tint = MaterialTheme.colors.onSurface
                )
                Text(
                    text = stringResource(id = R.string.select_category),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp)
                )
            }
        },
        text = {
            CategorySelector(incomeItems, expenseItems, isIncome, defaultCategory) {
                onAdd(it)
            }
        },
        confirmButton = {})
}

@Composable
private fun CategorySelector(
    incomeItems: List<TransactionCategory>,
    expenseItems: List<TransactionCategory>,
    isIncome: Boolean,
    defaultCategory: TransactionCategory? = null,
    onAdd: (category: TransactionCategory) -> Unit,
) {
    val isShowIncomeCategories = remember { mutableStateOf(isIncome) }
    val selectedOption = remember {
        mutableStateOf(defaultCategory)
    }
    val items = if (isShowIncomeCategories.value) incomeItems else expenseItems
    val context = LocalContext.current

    Column(Modifier.fillMaxWidth()) {
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
        LazyColumn {
            items(items.size) {
                val item = items[it]
                Row(
                    Modifier
                        .padding(4.dp)
                        .clickable {
                            selectedOption.value = item
                            onAdd(item)
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
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .weight(1f),
                        fontSize = 18.sp
                    )
                    RadioButton(
                        selected = (item.id == selectedOption.value?.id),
                        onClick = null
                    )
                }
            }
        }
    }
}