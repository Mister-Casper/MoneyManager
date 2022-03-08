package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.sgcdeveloper.moneymanager.util.Date
import java.time.LocalDate

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DatePicker(
    defaultDate: Date,
    onDateSelected: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
    isDarkTHeme: Boolean = true
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        modifier = Modifier.size(400.dp, 600.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        text = {
            MaterialDatePicker.Builder.datePicker()
                .setSelection(Date((defaultDate.getAsLocalDate()).plusDays(1)).epochMillis)
                .build()
                .apply {
                    addOnPositiveButtonClickListener { onDateSelected(Date(it).getAsLocalDate()) }
                    addOnDismissListener { onDismissRequest() }
                    show((context as FragmentActivity).supportFragmentManager, "tag")
                }

        },
        buttons = {})
}
