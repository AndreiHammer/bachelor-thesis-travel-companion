package eu.ase.travelcompanionapp.user.presentation.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
    var isInfoExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.currency),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CurrencyIcon(
                    currencyCode = selectedCurrency,
                    contentDescription = stringResource(R.string.select_currency),
                    modifier = Modifier.padding(end = 12.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.current_currency),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = if (selectedCurrency.isEmpty()) {
                            stringResource(R.string.use_hotel_currency)
                        } else {
                            "$selectedCurrency - ${Currency.getCurrencyName(selectedCurrency)}"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = { showCurrencyDialog = true }
                ) {
                    Text(text = stringResource(R.string.change))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider()

            IconButton(onClick = { isInfoExpanded = !isInfoExpanded }) {
                Icon(
                    imageVector = if (isInfoExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.toggle_filters)
                )
            }

            AnimatedVisibility(
                visible = isInfoExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = if (selectedCurrency.isEmpty()) {
                        stringResource(R.string.hotel_currency_info)
                    } else {
                        stringResource(R.string.preferred_currency_info)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
