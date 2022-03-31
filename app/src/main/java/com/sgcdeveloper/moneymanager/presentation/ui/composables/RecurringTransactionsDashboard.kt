package com.sgcdeveloper.moneymanager.presentation.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.AddRecurringTransaction
import com.sgcdeveloper.moneymanager.domain.model.BaseRecurringTransaction
import com.sgcdeveloper.moneymanager.domain.model.RecurringTransaction
import com.sgcdeveloper.moneymanager.presentation.theme.gray
import com.sgcdeveloper.moneymanager.presentation.theme.white

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecurringTransactionsDashboard(
    recurringTransactionEntry: List<BaseRecurringTransaction>,
    onClick: (recurringTransactionEntry: BaseRecurringTransaction) -> Unit
) {
    Card(
        Modifier
            .padding(top = 6.dp)
            .fillMaxWidth(), shape = RoundedCornerShape(12.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .heightIn(0.dp, 30000.dp)
                .padding(8.dp)
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.recurring_dashboard),
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .align(Alignment.CenterStart)
                    )
                }
            }
            items(recurringTransactionEntry.size) {
                when (val transaction = recurringTransactionEntry[it]) {
                    AddRecurringTransaction -> {
                        AddRecurringTransactionsItem {
                            onClick(transaction)
                        }
                    }
                    is RecurringTransaction -> {
                        RecurringItem(transaction){
                            onClick(transaction)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecurringItem(item:RecurringTransaction,onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            Card(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterVertically),
                shape = RoundedCornerShape(8.dp),
            ) {
                Box(modifier = Modifier.background(Color(item.transactionEntry.category.color))) {
                    Icon(
                        painter = painterResource(id = item.transactionEntry.category.icon),
                        contentDescription = "",
                        Modifier
                            .align(Alignment.Center)
                            .size(40.dp),
                        tint = white
                    )
                }
            }
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = item.transactionEntry.description, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = white)
                Text(text = stringResource(id = R.string.next_occurrence), fontSize = 14.sp)
                Text(text = item.nextTransactionDate, fontSize = 16.sp)
            }
            Text(
                text = item.money,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterVertically),
                color = Color(item.moneyColor)
            )
        }
    }
}

@Composable
fun AddRecurringTransactionsItem(onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(start = 12.dp, top = 12.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.add_icon),
            contentDescription = "add new recurring transaction",
            Modifier
                .align(
                    Alignment.CenterVertically
                )
                .border(2.dp, gray, RoundedCornerShape(6.dp)),
            tint = white
        )

        Text(
            text = stringResource(id = R.string.add_recurring), fontWeight = FontWeight.Bold, fontSize = 18.sp,
            modifier = Modifier
                .align(
                    Alignment.CenterVertically
                )
                .padding(start = 12.dp),
        )
    }
}