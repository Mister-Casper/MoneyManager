package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.theme.red
import com.sgcdeveloper.moneymanager.presentation.theme.white

@Composable
fun DeleteDialog(massage: String, onDelete: () -> Unit, onCancel: () -> Unit) {
    AlertDialog(
        containerColor = MaterialTheme.colors.background,
        onDismissRequest = onCancel,
        title = {
            Text(massage, color = MaterialTheme.colors.secondary)
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text(text = stringResource(id = R.string.cancel), color = white)
            }
        },
        confirmButton = {
            Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(backgroundColor = red)) {
                Text(text = stringResource(id = R.string.delete), color = white)
            }
        }
    )
}