package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.presentation.theme.white

@Composable
fun WalletPickerDialog(
    wallets: List<Wallet>? = null,
    defaultWallet: Wallet? = null,
    onAdd: (wallet: Wallet) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        containerColor = MaterialTheme.colors.background,
        onDismissRequest = onDismiss,
        title = {
            Row(Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { onDismiss() }
                )
                Text(
                    text = stringResource(id = R.string.select_wallet),
                    color = MaterialTheme.colors.secondary,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp)
                )
            }
        },
        text = {
            CategorySelector(wallets, defaultWallet) {
                onAdd(it)
                onDismiss()
            }
        },
        confirmButton = {})
}

@Composable
private fun CategorySelector(
    wallets: List<Wallet>? = null,
    defaultWallet: Wallet? = null,
    onAdd: (wallet: Wallet) -> Unit,
) {
    val selectedOption = remember {
        mutableStateOf(defaultWallet)
    }

    Column(Modifier.fillMaxWidth()) {
        LazyColumn {
            wallets?.let {
                items(wallets.size) {
                    val item = wallets[it]
                    Row(
                        Modifier
                            .padding(4.dp)
                            .clickable {
                                selectedOption.value = item
                                onAdd(item)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier
                                .size(48.dp)
                                .padding(4.dp)
                                .align(Alignment.CenterVertically),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Box(modifier = Modifier.background(Color(item.color))) {
                                androidx.compose.material.Icon(
                                    painter = painterResource(id = item.icon),
                                    contentDescription = "",
                                    Modifier
                                        .align(Alignment.Center)
                                        .size(32.dp),
                                    tint = white
                                )
                            }
                        }
                        Text(
                            text = item.name,
                            color = MaterialTheme.colors.secondary,
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .weight(1f),
                            fontSize = 18.sp
                        )
                        RadioButton(
                            selected = (item == selectedOption.value),
                            onClick = null
                        )
                    }
                }
            }
        }
    }
}