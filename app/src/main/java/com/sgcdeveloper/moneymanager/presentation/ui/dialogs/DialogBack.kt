package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.AddTransactionViewModel

@Composable
fun DialogBack(addViewModel: AddTransactionViewModel) {
    val dialogBackOpen = remember { addViewModel.backDialog }
    val signalBack = remember { addViewModel.back }
    if (dialogBackOpen.value) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = stringResource(R.string.confirm_action)) },
            text = { Text(text = stringResource(R.string.exit_without_saving)) },
            buttons = {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            dialogBackOpen.value = false
                            signalBack.value = true
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.exit)
                        )
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            dialogBackOpen.value = false
                            signalBack.value = false
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
}