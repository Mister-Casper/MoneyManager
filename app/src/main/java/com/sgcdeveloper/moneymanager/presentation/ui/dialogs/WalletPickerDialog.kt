package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.AddNewWallet
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.presentation.theme.gray
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.composables.AutoSizeText

var ywOffset = 0

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WalletPickerDialog(
    wallets: List<Wallet>? = null,
    defaultWallet: Wallet? = null,
    onAdd: (wallet: Wallet) -> Unit = {},
    onDismiss: () -> Unit = {},
    onAddNewWallet: (newWallet: Wallet) -> Unit = {}
) {
    AlertDialog(
        containerColor = MaterialTheme.colors.background,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                   if(offset.y <= ywOffset)
                       onDismiss()
                }
            }
            .customDialogModifier(),
        title = {
            Row(Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(32.dp)
                        .clickable { onDismiss() }
                )
                Text(
                    text = stringResource(id = R.string.select_wallet),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp)
                )
            }
        },
        text = {
            WalletSelector(wallets, defaultWallet, {
                onAdd(it)
                onDismiss()
            }, {
                onAddNewWallet(it)
            })
        },
        confirmButton = {}, properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

fun Modifier.customDialogModifier() = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    ywOffset = constraints.maxHeight - placeable.height
    layout(constraints.maxWidth, constraints.maxHeight) {
        placeable.place(0, ywOffset, 10f)
    }
}

@Composable
private fun WalletSelector(
    wallets: List<Wallet>? = null,
    defaultWallet: Wallet? = null,
    onAdd: (wallet: Wallet) -> Unit,
    onAddNewWallet: (newWallet: Wallet) -> Unit = {}
) {
    val context = LocalContext.current
    var selectedOption by remember {
        mutableStateOf(defaultWallet)
    }
    LazyColumn(Modifier.fillMaxWidth()) {
        wallets?.let {
            items(wallets.size) {
                val item = wallets[it]
                if (item is AddNewWallet) {
                    AddWalletItem {
                        onAddNewWallet(item)
                    }
                } else {
                    ExistWalletItem(context,item, selectedOption) {
                        selectedOption = item
                        onAdd(item)
                    }
                }
            }
        }
    }
}

@Composable
<<<<<<< HEAD
fun ExistWalletItem(context:Context,item: Wallet, selectedItem: Wallet?, onClick: () -> Unit) {
=======
fun ExistWalletItem(item: Wallet, selectedItem: Wallet?, onClick: () -> Unit) {
>>>>>>> parent of 480ad14 (Fixed bug)
    Row(
        Modifier
            .padding(4.dp)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .size(64.dp)
                .padding(4.dp)
                .align(Alignment.CenterVertically),
            shape = RoundedCornerShape(8.dp),
        ) {
            Box(modifier = Modifier.background(Color(item.color))) {
                Icon(
                    painter = painterResource(id = item.icon),
                    contentDescription = "",
                    Modifier
                        .align(Alignment.Center)
                        .size(48.dp),
                    tint = white
                )
            }
        }
        Column(
            Modifier
                .padding(start = 12.dp)
                .weight(1f)
        ) {
            AutoSizeText(
                text = item.name,
                suggestedFontSizes = listOf(18.sp, 16.sp, 14.sp, 2.sp)
            )

            AutoSizeText(
                text = item.formattedMoney,
                suggestedFontSizes = listOf(16.sp, 14.sp, 12.sp, 2.sp),
            )
        }
        RadioButton(
            selected = (item.walletId == selectedItem?.walletId),
            onClick = null
        )
    }
}

@Composable
fun AddWalletItem(onClick: () -> Unit) {
    Row(
        Modifier
            .padding(4.dp)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .size(64.dp)
                .padding(4.dp)
                .align(Alignment.CenterVertically),
            border = BorderStroke(2.dp, gray),
            shape = RoundedCornerShape(12.dp),
        ) {
            Box {
                Icon(
                    painter = painterResource(id = R.drawable.add_icon),
                    contentDescription = "add new wallet",
                    Modifier.align(
                        Alignment.Center
                    ),
                    tint = white
                )
            }
        }
        Text(
            text = stringResource(id = R.string.add_new_wallet),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
                .fillMaxWidth()
        )
        RadioButton(
            selected = false,
            onClick = null
        )
    }
}