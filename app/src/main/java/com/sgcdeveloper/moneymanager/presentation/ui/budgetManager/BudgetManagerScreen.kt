package com.sgcdeveloper.moneymanager.presentation.ui.budgetManager

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.BaseBudget
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.theme.Typography
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import com.sgcdeveloper.moneymanager.presentation.theme.dark_gray
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.composables.RoundedLinearProgressIndicator
import com.sgcdeveloper.moneymanager.presentation.ui.homeScreen.HomeViewModel

@Composable
fun BudgetManagerScreen(homeViewModel: HomeViewModel, navController: NavController) {
    val budgets = remember { homeViewModel.state }.value.budgets

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth()) {
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
                        text = stringResource(id = R.string.budget_view),
                        fontSize = 24.sp,
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 12.dp)
                    )
                }
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (budgets.size > 1) {
                    items(budgets.size) {
                        val budget = budgets[it]
                        if (budget is BaseBudget.BudgetHeader) {
                            BudgetHeader(budget) {
                                navController.navigate(Screen.TimeIntervalBudgetManager(budget.period).route)
                            }
                        } else if (budget is BaseBudget.BudgetItem) {
                            BudgetItem(budget) {
                                navController.navigate(Screen.BudgetScreen(budget).route)
                            }
                        }
                    }
                }else {
                    item {
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
                                    text = stringResource(id = R.string.no_budgets),
                                    style = Typography.h5,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                                Text(
                                    text = stringResource(id = R.string.tap_to_add_budget),
                                    fontWeight = FontWeight.Thin,
                                    textAlign = TextAlign.Center,
                                    fontSize = 14.sp,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                }

                item {
                    AndroidView(
                        modifier = Modifier.fillMaxWidth(),
                        factory = { context ->
                            AdView(context).apply {
                                adSize = AdSize.LARGE_BANNER
                                adUnitId = "ca-app-pub-5494709043617393/2510789678"
                                loadAd(AdRequest.Builder().build())
                            }
                        }
                    )
                    Spacer(modifier = Modifier.padding(top = 64.dp))
                }
            }
        }
        OutlinedButton(
            onClick = { navController.navigate(Screen.AddBudgetScreen().route) },
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = blue)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add_icon),
                contentDescription = "",
                tint = white,
                modifier = Modifier.size(32.dp)
            )
            Text(text = stringResource(id = R.string.add_budget))
        }
    }
}

@Composable
fun BudgetHeader(budget: BaseBudget.BudgetHeader, onClick: () -> Unit) {
    Column(
        Modifier
            .clickable { onClick() }
            .padding(bottom = 8.dp, top = 8.dp)
            .fillMaxWidth()
    ) {
        Divider()
        Text(
            text = budget.header,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colors.secondary
        )
        Text(
            text = budget.periodDescription,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 14.sp,
            color = MaterialTheme.colors.secondary
        )
        Text(
            text = budget.total,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 14.sp,
            color = MaterialTheme.colors.secondary
        )
        Divider()
    }
}

@Composable
fun BudgetItem(budget: BaseBudget.BudgetItem, onClick: () -> Unit) {
    Column(
        Modifier
            .padding(8.dp)
            .clickable { onClick() }) {
        Text(text = budget.budgetName, color = MaterialTheme.colors.secondary)
        Text(text = budget.periodDescription, color = MaterialTheme.colors.secondary)
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
        ) {
            Text(
                text = stringResource(id = R.string.spent),
                modifier = Modifier.align(Alignment.CenterStart),
                color = MaterialTheme.colors.secondary
            )
            Text(
                text = stringResource(id = budget.leftStrRes, ""),
                modifier = Modifier.align(Alignment.CenterEnd),
                color = MaterialTheme.colors.secondary
            )
        }
        Box(Modifier.fillMaxWidth()) {
            Text(
                text = budget.spent,
                Modifier.align(Alignment.CenterStart),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.secondary
            )
            Text(
                text = budget.left,
                Modifier.align(Alignment.CenterEnd),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.secondary
            )
        }
        RoundedLinearProgressIndicator(
            height = 8.dp,
            progress = budget.progress,
            color = Color(budget.color),
            backgroundColor = dark_gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp)
        )
    }
}