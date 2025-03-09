package eu.ase.travelcompanionapp.core.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.core.domain.utils.DateUtils
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerWithDialog(
    modifier: Modifier = Modifier,
    label: String,
    selectedDate: String,
    minDate: Long? = null,
    onDateSelected: (String) -> Unit
) {
    val dateUtils = DateUtils()
    val todayStart = remember {
        LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
    val effectiveMinDate = remember(minDate) {
        minDate ?: todayStart
    }

    var selectedDateMillis by remember { mutableLongStateOf(-1L) }

    if (selectedDate.isNotEmpty() && selectedDateMillis == -1L) {
        dateUtils.parseDisplayDate(selectedDate)?.let { zonedDateTime ->
            selectedDateMillis = zonedDateTime.toInstant().toEpochMilli()
        }
    }


    val dateState = rememberDatePickerState(
            initialSelectedDateMillis = if (selectedDateMillis > 0) selectedDateMillis else null,
            yearRange = IntRange(LocalDate.now().year, LocalDate.now().year + 2),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val dateToCheck = Instant.ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                    val minLocalDate = Instant.ofEpochMilli(effectiveMinDate)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    return !dateToCheck.isBefore(minLocalDate)
                }
            }
        )


    val dateToString = selectedDate.ifEmpty { label }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { showDialog = true })
                .padding(16.dp),
            text = dateToString,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )

        if (showDialog) {
            DatePickerDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            dateState.selectedDateMillis?.let {
                                val setDate = dateUtils.convertMillisToLocalDate(it)
                                val selectedLocalDate = setDate.toLocalDate()
                                val minLocalDate = Instant.ofEpochMilli(effectiveMinDate)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                val isValid = !selectedLocalDate.isBefore(minLocalDate)
                                if (isValid) {
                                    selectedDateMillis = it
                                    val formattedDate = dateUtils.dateToString(setDate)
                                    onDateSelected(formattedDate)
                                    showDialog = false
                                }
                            } ?: run {
                                showDialog = false
                            }
                        }
                    ) {
                        Text(text = stringResource(R.string.confirm))
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text(text = stringResource(R.string.cancel))
                    }
                },
                content = {
                    DatePicker(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        state = dateState,
                        showModeToggle = true
                    )
                }
            )
        }
    }
}



