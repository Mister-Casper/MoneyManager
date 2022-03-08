package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.AddTransactionViewModel

@Composable
fun DialogBack(dialogBackOpen: Boolean, signalBack: Boolean,
               signalReturn: (Boolean)-> Unit,
               dialogOpen: (Boolean)-> Unit,) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = stringResource(R.string.confirm_action), color = MaterialTheme.colors.secondary) },
        text = { Text(text = stringResource(R.string.exit_without_saving)) },
        buttons = {
            Row(
                modifier = Modifier.padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {signalReturn(dialogBackOpen)
                        dialogOpen(signalBack)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.exit)
                    )
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {signalReturn(dialogBackOpen)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.retur)
                    )
                }
            }
        }
    )
}
