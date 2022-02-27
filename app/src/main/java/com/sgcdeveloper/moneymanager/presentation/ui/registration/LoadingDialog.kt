package com.sgcdeveloper.moneymanager.presentation.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sgcdeveloper.moneymanager.R

@Composable
fun LoadingDialog(isShowLoadingDialog: Boolean) {
    if (isShowLoadingDialog){
        Dialog(
            onDismissRequest = { },
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colors.background, shape = RoundedCornerShape(8.dp))
            ) {
                Row() {
                    CircularProgressIndicator(modifier = Modifier.padding(6.dp, 0.dp, 0.dp, 0.dp))
                    Text(
                        text = stringResource(R.string.loading),
                        modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterVertically),
                        color = MaterialTheme.colors.secondary)
                }
            }
        }
    }
}