package com.sgcdeveloper.moneymanager.presentation.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.presentation.theme.Typography
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import com.sgcdeveloper.moneymanager.presentation.theme.red
import com.sgcdeveloper.moneymanager.presentation.ui.composables.SearchBar
import com.sgcdeveloper.moneymanager.presentation.ui.composables.rememberSearchState
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.SelectCategoriesDialog
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.SelectTimeIntervalDialog
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.WalletSelectorDialog
import com.sgcdeveloper.moneymanager.presentation.ui.transactions.TransactionItem

@Composable
fun SearchTransactionsScreen(navController: NavController, searchTransactionsViewModel: SearchTransactionsViewModel) {
    val state = remember { searchTransactionsViewModel.state }.value

    if(state.dialogState is DialogState.SelectCustomTImeIntervalDialog){
        SelectTimeIntervalDialog({searchTransactionsViewModel.closeDialog()},state.dialogState.startDate,state.dialogState.endDate,{start,end->
            val timeController = TimeIntervalController.CustomController()
            timeController.startIntervalDate = start
            timeController.endIntervalDate = end
            searchTransactionsViewModel.updateTime(timeController)
            searchTransactionsViewModel.closeDialog()
        })
    }else if(state.dialogState is DialogState.SelectCategoriesDialog){
        SelectCategoriesDialog(categories = searchTransactionsViewModel.categories, defaultCategories = state.categories,{
            searchTransactionsViewModel.updateCategories(it)
            searchTransactionsViewModel.closeDialog()
        }){
            searchTransactionsViewModel.closeDialog()
        }
    }else if(state.dialogState is DialogState.SelectWalletsDialog){
        WalletSelectorDialog(wallets = searchTransactionsViewModel.wallets, defaultWallets = state.wallets,{
            searchTransactionsViewModel.updateWallets(it)
            searchTransactionsViewModel.closeDialog()
        },{
            searchTransactionsViewModel.closeDialog()
        })
    }

    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
        ) {
            Row(Modifier.fillMaxWidth()) {
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
                Spacer(modifier = Modifier.width(12.dp))
                val state = rememberSearchState()
                SearchBar(
                    query = state.query.value,
                    onQueryChange = {
                        state.query.value = it
                        searchTransactionsViewModel.updateText(it.text)
                    },
                    onSearchFocusChange = {
                        state.focused.value = it
                    },
                    onClearQuery = {
                        state.query.value = TextFieldValue("")
                        searchTransactionsViewModel.updateText("")
                    },
                    onBack = {
                        state.query.value = TextFieldValue("")
                        searchTransactionsViewModel.updateText("")
                    },
                    searching = state.searching.value,
                    focused = false,
                )
            }
            LazyRow {
                item {
                    FilterItem(stringResource(id = R.string.date_search), state.dateText, state.isDateActive,{
                        searchTransactionsViewModel.showSelectDateDialog()
                    }){
                        searchTransactionsViewModel.clearDate()
                    }
                    FilterItem(stringResource(id = R.string.category_search), state.categoryText, state.isCategoriesActive,{
                        searchTransactionsViewModel.showSelectCategoriesDialog()
                    }){
                        searchTransactionsViewModel.clearCategories()
                    }
                    FilterItem(stringResource(id = R.string.wallet_search), state.walletText, state.isWalletsActive,{
                        searchTransactionsViewModel.showSelectWalletsDialog()
                    }){
                        searchTransactionsViewModel.clearWallets()
                    }
                }
            }
            if(!state.isExistAny){
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(Modifier.align(Alignment.Center)) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                painter = painterResource(id = R.drawable.empty_icon),
                                contentDescription = "",
                                Modifier
                                    .align(Alignment.CenterVertically)
                                    .weight(2f)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        Text(
                            text = stringResource(id = R.string.no_result_found),
                            style = Typography.h5,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }else{
                LazyColumn{
                    item {
                        Card(Modifier.fillMaxWidth()) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(6.dp)
                                    .padding(start = 12.dp)
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp)
                                ) {
                                    Text(
                                        text = state.countTransactions,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier
                                            .weight(1f),
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                Text(
                                    text = stringResource(id = R.string.overview),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 16.dp),
                                    fontSize = 18.sp
                                )
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.income),
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = state.income
                                    )
                                }
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.expense),
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(text = state.expense, color = red)
                                }
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.total),
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = state.total
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    items(state.transactions.size){
                        val item = state.transactions[it]
                        TransactionItem(item,navController,false,{},{})
                    }
                }
            }
        }
    }
}

@Composable
fun FilterItem(
    defaultText: String,
    selectedText: String,
    isActive: Boolean,
    onClicked: () -> Unit,
    onCanceled: () -> Unit
) {
    val text = if (isActive) selectedText else defaultText
    val icon = if (isActive) Icons.Filled.Cancel else Icons.Filled.KeyboardArrowDown
    val background = if (isActive) blue else MaterialTheme.colors.surface

    Card(
        Modifier
            .padding(12.dp)
            .clickable { onClicked() }, backgroundColor = background) {
        Row(Modifier.padding(8.dp)) {
            Text(text = text, modifier = Modifier.align(Alignment.CenterVertically))
            Spacer(modifier = Modifier.width(4.dp))
            if (isActive) {
                Icon(imageVector = icon, contentDescription = "", Modifier.clickable {
                        onCanceled()
                })
            }else
                Icon(imageVector = icon, contentDescription = "")
        }
    }
}