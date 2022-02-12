package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.composables.WalletCard

@Composable
fun DeleteWalletDialog(wallet: Wallet?=null, onDelete: () -> Unit, onCancel: () -> Unit,titleId:Int = R.string.are_u_sure_delete_wallet) {
    AlertDialog(
        containerColor = MaterialTheme.colors.background,
        onDismissRequest = onCancel,
        title = {
            Text(text = stringResource(id = titleId))
        },
        text = {
            wallet?.let {
                Column(Modifier.fillMaxWidth()) {
                    WalletCard(wallet)
                }
            }
        },
        confirmButton = {
            Button(onClick = onDelete) {
                Text(text = stringResource(id = R.string.delete), color = white)
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text(text = stringResource(id = R.string.cancel), color = white)
            }
        })
}