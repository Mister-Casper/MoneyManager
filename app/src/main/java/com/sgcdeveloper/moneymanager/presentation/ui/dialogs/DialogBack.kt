package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.runtime.Composable

@Composable
fun DialogBack(dialogBackOpen: Boolean, signalBack: Boolean,
               signalReturn: (Boolean)-> Unit,
               dialogOpen: (Boolean)-> Unit,) {
    dialogOpen(signalBack)
}
