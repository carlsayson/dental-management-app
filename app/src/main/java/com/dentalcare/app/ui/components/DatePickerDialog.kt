package com.dentalcare.app.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.dentalcare.app.util.DateUtils
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    initialDate: String? = null
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate?.let { 
            DateUtils.parseDate(it)?.time 
        } ?: System.currentTimeMillis()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = DateUtils.formatDate(millis)
                        onDateSelected(date)
                    }
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
