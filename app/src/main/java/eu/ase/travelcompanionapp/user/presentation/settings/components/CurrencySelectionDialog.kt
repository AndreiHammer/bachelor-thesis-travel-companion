package eu.ase.travelcompanionapp.user.presentation.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.user.domain.model.Currency

@Composable
fun CurrencySelectionDialog(
    currencies: List<Currency>,
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxSize(0.8f),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = stringResource(R.string.select_currency),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )

                HorizontalDivider()

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(currencies) { currency ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCurrencySelected(currency.code) }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CurrencyIcon(
                                currencyCode = currency.code,
                                modifier = Modifier.padding(end = 12.dp)
                            )

                            Text(
                                text = "${currency.code} - ${currency.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )

                            if (currency.code == selectedCurrency) {
                                Text(
                                    text = "✓",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        if (currencies.indexOf(currency) < currencies.size - 1) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }

                HorizontalDivider()

                Text(
                    text = stringResource(R.string.cancel),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDismiss() }
                        .padding(16.dp)
                )
            }
        }
    }
}