package com.sgcdeveloper.moneymanager.presentation.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.AddNewWallet
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.presentation.theme.gray
import com.sgcdeveloper.moneymanager.presentation.theme.white

@Composable
fun ColumnScope.WalletCard(wallet: Wallet, onClick: (wallet: Wallet) -> Unit) {
    Card(
        modifier = Modifier
            .size(120.dp)
            .padding(8.dp)
            .align(Alignment.CenterHorizontally)
            .clickable { onClick(wallet) },
        border = BorderStroke(2.dp, gray),
        shape = RoundedCornerShape(12.dp),
    ) {
        if (wallet is AddNewWallet) {
            Icon(
                painter = painterResource(id = R.drawable.add_icon),
                contentDescription = "add new wallet",
                Modifier.align(
                    Alignment.CenterHorizontally
                ),
                tint = MaterialTheme.colors.secondary
            )
        } else {
            Column(modifier = Modifier.background(Color(wallet.color))) {
                Icon(
                    painter = painterResource(id = wallet.icon),
                    contentDescription = "wallet icon",
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(48.dp)
                        .padding(top = 4.dp),
                    tint = white
                )
                AutoSizeText( text = wallet.name, suggestedFontSizes = listOf(20.sp,18.sp,16.sp,14.sp,12.sp,10.sp,8.sp,6.sp,4.sp),color = white)
                AutoSizeText(text = wallet.formattedMoney, suggestedFontSizes = listOf(22.sp,20.sp,18.sp,16.sp,14.sp,12.sp,10.sp,8.sp,6.sp,4.sp),color = white)
            }
        }
    }
}