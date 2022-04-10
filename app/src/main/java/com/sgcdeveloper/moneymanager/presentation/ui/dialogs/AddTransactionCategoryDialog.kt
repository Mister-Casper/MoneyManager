package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.TransactionCategory
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.composables.ColorPicker
import com.sgcdeveloper.moneymanager.presentation.ui.composables.IconPicker
import com.sgcdeveloper.moneymanager.presentation.ui.composables.InputField
import com.sgcdeveloper.moneymanager.util.categories_icons

@Composable
fun AddTransactionCategoryDialog(
    category: TransactionCategory,
    isExpense: Boolean,
    onAdd: (category: TransactionCategory) -> Unit,
    onCancel: () -> Unit
) {
    var icon by remember { mutableStateOf(category.icon) }
    var color by remember { mutableStateOf(category.color) }
    var description by remember { mutableStateOf(category.description) }

    AlertDialog(
        containerColor = MaterialTheme.colors.background,
        onDismissRequest = onCancel,
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(id = R.string.add_category), Modifier.align(Alignment.CenterStart))
                Button(
                    onClick = { onAdd(category.copy(icon = icon, color = color, description = description)) },
                    Modifier.align(Alignment.CenterEnd)
                ) {
                    Text(text = stringResource(id = R.string.save))
                }
            }
        },
        text = {
            Column(Modifier.fillMaxSize()) {
                Box(
                    Modifier
                        .padding(4.dp),
                ) {
                    Row(Modifier.align(Alignment.CenterStart)) {
                        Card(
                            modifier = Modifier
                                .size(48.dp)
                                .padding(4.dp)
                                .align(Alignment.CenterVertically),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Box(modifier = Modifier.background(Color(color))) {
                                Icon(
                                    painter = painterResource(id = icon),
                                    contentDescription = "",
                                    Modifier
                                        .align(Alignment.Center)
                                        .size(32.dp),
                                    tint = white
                                )
                            }
                        }
                        Text(
                            text = description,
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .weight(1f),
                            fontSize = 18.sp
                        )
                    }
                }
                InputField(
                    description,
                    { description = it },
                    stringResource(id = R.string.category_description),
                    false,
                    ""
                )
                Text(
                    text = stringResource(id = R.string.category_color),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp),
                    color = MaterialTheme.colors.onBackground
                )
                ColorPicker(40.dp, color) {
                    color = it
                }
                Text(
                    text = stringResource(id = R.string.category_icon),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp),
                    color = MaterialTheme.colors.onBackground
                )
                IconPicker(categories_icons, 40.dp, icon) {
                    icon = it
                }
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        confirmButton = {}
    )
}