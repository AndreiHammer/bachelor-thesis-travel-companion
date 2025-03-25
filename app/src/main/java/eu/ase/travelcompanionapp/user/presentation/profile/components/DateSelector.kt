package eu.ase.travelcompanionapp.user.presentation.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.core.presentation.DatePickerWithDialog

@Composable
fun DateSelector(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.birth_date),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
            )
        ) {
            DatePickerWithDialog(
                modifier = Modifier.fillMaxWidth(),
                label = selectedDate.ifEmpty { stringResource(R.string.select_birth_date) },
                selectedDate = selectedDate,
                onDateSelected = onDateSelected,
                allowPastDates = true
            )
        }
    }
}