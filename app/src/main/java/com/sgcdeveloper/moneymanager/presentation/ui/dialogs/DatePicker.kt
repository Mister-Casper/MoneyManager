package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.material.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import com.sgcdeveloper.moneymanager.util.Date
import java.time.LocalDate
import java.util.*

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
        properties = DialogProperties(usePlatformDefaultWidth = false),
        text = {
            val dialog = DatePickerDialog(
                context,
                { q: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    val cal = Calendar.getInstance()
                    cal.set(year, month, dayOfMonth)
                    onDateSelected(Date(cal.timeInMillis).getAsLocalDate())
                    onDismissRequest()
                },
                defaultDate.getAsLocalDate().year,
                defaultDate.getAsLocalDate().monthValue - 1,
                defaultDate.getAsLocalDate().dayOfMonth
            )
            dialog.show()
            dialog.setOnDismissListener {
                onDismissRequest()
            }
        },
        buttons = {})
}
