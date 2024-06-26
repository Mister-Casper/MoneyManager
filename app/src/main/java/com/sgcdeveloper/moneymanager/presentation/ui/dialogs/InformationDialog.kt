package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sgcdeveloper.moneymanager.R

@Composable
fun InformationDialog(information: String,  onCancel: () -> Unit) {
    AlertDialog(
        containerColor = MaterialTheme.colors.background,
        onDismissRequest = onCancel,
        title = {
            Text(information)
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        confirmButton = {}
    )
}