package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.Currency
import com.sgcdeveloper.moneymanager.presentation.ui.composables.SearchBar
import com.sgcdeveloper.moneymanager.presentation.ui.composables.rememberSearchState

@Composable
fun SelectCurrenciesDialog(
    currencies: List<Currency>,
    defaultCurrency: Currency,
    onAdd: (currency: Currency) -> Unit,
    onDismiss: () -> Unit = {}
) {
    val currencyItems = remember { currencies.toMutableList() }

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
                    text = stringResource(id = R.string.select_currency),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp)
                )
            }
        },
        text = {
            val state = rememberSearchState()
            Column(Modifier.fillMaxWidth()) {
                SearchBar(
                    query = state.query.value,
                    onQueryChange = {
                        state.query.value = it
                        currencyItems.clear()
                        currencyItems.addAll(
                            currencies.filter { currency ->
                                val currencyName = currency.name.replace(" ", "")
                                val searchCurrencyName = it.text.replace(" ", "")
                                val isContains = currencyName.contains(searchCurrencyName, ignoreCase = true)
                                isContains
                            }
                        )
                    },
                    onSearchFocusChange = {
                        state.focused.value = it
                    },
                    onClearQuery = {
                        state.query.value = TextFieldValue("")
                        currencyItems.clear()
                        currencyItems.addAll(0, currencies)
                    },
                    onBack = {
                        state.query.value = TextFieldValue("")
                        currencyItems.clear()
                        currencyItems.addAll(0, currencies)
                    },
                    searching = state.searching.value,
                    focused = state.focused.value,
                )

                RadioGroup(currencyItems, defaultCurrency) {
                    onAdd(it)
                }
            }
        },
        confirmButton = {})
}

@Composable
private fun RadioGroup(
    currencies: List<Currency> = listOf(),
    defaultValue: Currency,
    onAdd: (currency: Currency) -> Unit,
) {
    val (selectedOption) = remember {
        mutableStateOf(defaultValue)
    }

    LazyColumn {
        items(currencies.size) {
            val item = currencies[it]
            Row(
                Modifier.padding(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (item == selectedOption),
                    onClick = null
                )

                val annotatedname = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(fontWeight = FontWeight.Bold),
                    ) { append("  ${item.name}  ") }
                }

                ClickableText(
                    text = annotatedname,
                    onClick = {
                        onAdd(item)
                    },
                    style = TextStyle(color = MaterialTheme.colors.onSurface),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}