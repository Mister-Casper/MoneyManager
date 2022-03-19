package com.sgcdeveloper.moneymanager.presentation.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.BaseBudget

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BudgetDashboard(budgets: List<BaseBudget>, onClick: (budget: BaseBudget) -> Unit, onManageClick:()->Unit) {
    Card(
        Modifier
            .padding(top = 6.dp)
            .fillMaxWidth(), shape = RoundedCornerShape(12.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(8.dp)
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.budget),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 12.dp).align(Alignment.CenterStart)
                    )
                    Text(
                        text = stringResource(id = R.string.manage),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.padding(end = 24.dp).align(Alignment.CenterEnd)
                            .clickable { onManageClick() }
                    )
                }
            }
            items(budgets.size){

            }
        }
    }
}