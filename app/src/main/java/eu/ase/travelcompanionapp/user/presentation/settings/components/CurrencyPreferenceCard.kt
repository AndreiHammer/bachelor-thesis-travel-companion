package eu.ase.travelcompanionapp.user.presentation.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.user.domain.model.Currency

@Composable
fun CurrencyPreferenceCard(
    currencies: List<Currency>,
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit
) {
    var showCurrencyDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.currency_preferences),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedCurrency.isEmpty(),
                    onClick = { onCurrencySelected("") }
                )
                Text(
                    text = stringResource(R.string.use_hotel_currency),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCurrencyDialog = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.preferred_currency),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = if (selectedCurrency.isEmpty()) {
                        stringResource(R.string.use_hotel_currency)
                    } else {
                        "$selectedCurrency (${Currency.getCurrencyName(selectedCurrency)})"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                CurrencyIcon(
                    currencyCode = selectedCurrency,
                    contentDescription = stringResource(R.string.select_currency)
                )
            }

            if (showCurrencyDialog) {
                CurrencySelectionDialog(
                    currencies = currencies,
                    selectedCurrency = selectedCurrency,
                    onCurrencySelected = {
                        onCurrencySelected(it)
                        showCurrencyDialog = false
                    },
                    onDismiss = { showCurrencyDialog = false }
                )
            }
        }
    }
}
