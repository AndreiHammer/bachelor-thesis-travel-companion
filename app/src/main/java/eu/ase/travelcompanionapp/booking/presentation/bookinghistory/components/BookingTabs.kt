package eu.ase.travelcompanionapp.booking.presentation.bookinghistory.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import eu.ase.travelcompanionapp.R

@Composable
fun BookingTabs(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = modifier
    ) {
        Tab(
            selected = selectedTabIndex == 0,
            onClick = { onTabSelected(0) },
            text = { 
                Text(
                    text = stringResource(R.string.active_reservations),
                    style = MaterialTheme.typography.bodyMedium
                ) 
            }
        )
        Tab(
            selected = selectedTabIndex == 1,
            onClick = { onTabSelected(1) },
            text = { 
                Text(
                    text = stringResource(R.string.past_reservations),
                    style = MaterialTheme.typography.bodyMedium
                ) 
            }
        )
    }
} 