package com.sgcdeveloper.moneymanager.presentation.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
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
import com.sgcdeveloper.moneymanager.domain.model.Wallet

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WalletDashboard(wallets: List<Wallet>, onClick: (wallet: Wallet) -> Unit,onManageClick:()->Unit) {
    Card(
        Modifier
            .padding(top = 6.dp)
            .fillMaxWidth(), shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.wallet),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 12.dp).align(Alignment.CenterStart)
                )
                Text(
                    text = stringResource(id = R.string.manage),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(end = 24.dp).align(Alignment.CenterEnd).clickable{onManageClick()}
                )
            }
            LazyVerticalGrid(
                cells = GridCells.Adaptive(120.dp),
                userScrollEnabled = false,
                // Костыль чтобы не конфликтовали LazyColumn и LazyVerticalGrid
                modifier = Modifier.heightIn(0.dp, 30000.dp)
            ) {
                items(wallets.size) {
                    val wallet = wallets[it]
                    WalletCard(wallet) {
                        onClick(it)
                    }
                }
            }
        }
    }
}