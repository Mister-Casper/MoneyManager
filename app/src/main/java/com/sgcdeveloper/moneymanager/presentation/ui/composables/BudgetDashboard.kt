package com.sgcdeveloper.moneymanager.presentation.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.sgcdeveloper.moneymanager.domain.model.BaseBudget
import com.sgcdeveloper.moneymanager.presentation.theme.dark_gray
import com.sgcdeveloper.moneymanager.presentation.theme.gray
import com.sgcdeveloper.moneymanager.presentation.theme.white

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BudgetDashboard(budgets: List<BaseBudget>, onClick: (budget: BaseBudget) -> Unit, onManageClick: () -> Unit) {
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
                        text = stringResource(id = R.string.budget),
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .align(Alignment.CenterStart)
                    )
                    Text(
                        text = stringResource(id = R.string.manage),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .padding(end = 24.dp)
                            .align(Alignment.CenterEnd)
                            .clickable { onManageClick() }
                    )
                }
            }
            items(budgets.size) {
                val budget = budgets[it]
                when (budget) {
                    is BaseBudget.AddNewBudget -> {
                        AddBudgetItem { onClick(budget) }
                    }
                    is BaseBudget.BudgetHeader -> {
                        Spacer(modifier = Modifier.height(6.dp))
                        BudgetHeader(budget)
                    }
                    is BaseBudget.BudgetItem -> {
                        BudgetItem(budget) { onClick(budget) }
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetItem(budgetItem: BaseBudget.BudgetItem, onClick: () -> Unit) {
    Card {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 6.dp)
                .clickable { onClick() }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = budgetItem.budgetName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(
                        Alignment.CenterStart
                    )
                )
                Text(
                    text = stringResource(id = budgetItem.leftStrRes, budgetItem.left),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.align(
                        Alignment.CenterEnd
                    )
                )
            }
            RoundedLinearProgressIndicator(
                height = 8.dp,
                progress = budgetItem.progress,
                color = Color(budgetItem.color),
                backgroundColor = dark_gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 12.dp)
            )
        }
    }
}

@Composable
fun BudgetHeader(budgetHeader: BaseBudget.BudgetHeader) {
    Card (Modifier.fillMaxWidth()){
        Text(
            text = budgetHeader.periodName,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            modifier = Modifier.padding(start = 12.dp, top = 12.dp)
        )
    }
}

@Composable
fun AddBudgetItem(onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(start = 12.dp, top = 12.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.add_icon),
            contentDescription = "add new budget",
            Modifier
                .align(
                    Alignment.CenterVertically
                )
                .size(40.dp)
                .border(2.dp, gray, RoundedCornerShape(6.dp)),
            tint = white
        )

        Text(
            text = stringResource(id = R.string.add_budget), fontWeight = FontWeight.Bold, fontSize = 18.sp,
            modifier = Modifier
                .align(
                    Alignment.CenterVertically
                )
                .padding(start = 12.dp),
        )
    }
}