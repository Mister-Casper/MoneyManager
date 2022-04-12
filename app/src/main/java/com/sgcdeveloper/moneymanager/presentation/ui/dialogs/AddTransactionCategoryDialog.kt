package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionCategoryEntry
import com.sgcdeveloper.moneymanager.domain.model.TransactionCategory
import com.sgcdeveloper.moneymanager.presentation.theme.gray
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.composables.AutoSizeText
import com.sgcdeveloper.moneymanager.presentation.ui.composables.ColorPicker
import com.sgcdeveloper.moneymanager.presentation.ui.composables.IconPicker
import com.sgcdeveloper.moneymanager.presentation.ui.composables.InputField
import com.sgcdeveloper.moneymanager.presentation.ui.init.InitViewModel.Companion.MAX_CUSTOM_CATEGORY_LENGTH
import com.sgcdeveloper.moneymanager.util.categories_icons

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddTransactionCategoryDialog(
    category: TransactionCategory,
    isExpense: Boolean,
    onAdd: (category: TransactionCategoryEntry) -> Unit,
    onCancel: () -> Unit
) {
    var icon by remember { mutableStateOf(category.icon) }
    var color by remember { mutableStateOf(category.color) }
    var description by remember { mutableStateOf(category.description) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    AlertDialog(
        containerColor = MaterialTheme.colors.background,
        onDismissRequest = onCancel,
        title = {
            Column(Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.align(Alignment.CenterStart)) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            contentDescription = "",
                            tint = MaterialTheme.colors.onBackground,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 6.dp)
                                .size(32.dp)
                                .clickable {
                                    onCancel()
                                }
                        )
                        Text(
                            stringResource(id = R.string.add_category),
                            Modifier
                                .align(Alignment.CenterVertically)
                                .padding(6.dp),
                            fontSize = 18.sp
                        )
                    }
                    Button(
                        onClick = {
                            onAdd(
                                TransactionCategoryEntry(
                                    id = category.id,
                                    color = color.toString(),
                                    icon = context.resources.getResourceEntryName(icon),
                                    description = description,
                                    isDefault = if (category.isDefault) 1 else 0,
                                    isExpense = if (isExpense) 1 else 0,
                                    order = category.order
                                )
                            )
                        },
                        Modifier.align(Alignment.CenterEnd),
                        enabled = description.isNotEmpty()
                    ) {
                        Text(text = stringResource(id = R.string.save))
                    }
                }
                Box(
                    Modifier
                        .padding(4.dp),
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterStart)
                            .border(
                                border = BorderStroke(2.dp, gray),
                                shape = RoundedCornerShape(12.dp),
                            )
                    ) {
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
                        AutoSizeText(
                            text = description,
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .align(Alignment.CenterVertically),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        },
        text = {
            LazyColumn(Modifier.fillMaxSize()) {
                item {
                    Column(Modifier.fillMaxSize()) {
                        InputField(
                            description,
                            { if (it.length <= MAX_CUSTOM_CATEGORY_LENGTH) description = it },
                            stringResource(id = R.string.category_description),
                            false,
                            "",
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = {
                                keyboardController?.hide()
                            })
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
                            icon = categories_icons[it]
                        }
                    }
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