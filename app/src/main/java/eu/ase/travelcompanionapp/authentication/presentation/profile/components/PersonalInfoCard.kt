package eu.ase.travelcompanionapp.authentication.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.R

@Composable
fun PersonalInfoCard(
    name: String,
    email: String,
    phoneNumber: String,
    birthDate: String,
    gender: String,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.personal_information),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.full_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                }
            )

            OutlinedTextField(
                value = email,
                onValueChange = { },
                label = { Text(stringResource(R.string.email)) },
                enabled = false,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null)
                }
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneChange,
                label = { Text(stringResource(R.string.phone_number)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null)
                }
            )

            DateSelector(
                selectedDate = birthDate,
                onDateSelected = onBirthDateChange
            )

            GenderSelector(
                selectedGender = gender,
                onGenderSelected = onGenderChange
            )

            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Done, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.save_changes))
            }
        }
    }
}