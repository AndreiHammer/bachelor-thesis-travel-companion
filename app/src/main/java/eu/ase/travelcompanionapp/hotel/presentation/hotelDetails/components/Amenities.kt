package eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AmenitiesSection(
    amenities: List<String>,
    modifier: Modifier = Modifier
) {
    if (amenities.isEmpty()) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.amenities),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = modifier.fillMaxWidth(),
                maxItemsInEachRow = 3
            ) {
                amenities.forEach { amenity ->
                    AmenityChip(amenity = amenity)
                }
            }
        }
    }
}

@Composable
fun AmenityChip(amenity: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = when {
                amenity.contains("WIFI", ignoreCase = true) -> painterResource(R.drawable.wifi)
                amenity.contains("SWIMMING_POOL", ignoreCase = true) -> painterResource(R.drawable.pool)
                amenity.contains("SPA", ignoreCase = true) -> painterResource(R.drawable.spa)
                amenity.contains("PARKING", ignoreCase = true) -> painterResource(R.drawable.park)
                amenity.contains("FITNESS_CENTER", ignoreCase = true) -> painterResource(R.drawable.baseline_fitness_center_24)
                amenity.contains("ROOM_SERVICE", ignoreCase = true) -> painterResource(R.drawable.baseline_restaurant_24)
                amenity.contains("RESTAURANT", ignoreCase = true) -> painterResource(R.drawable.baseline_restaurant_24)
                else -> painterResource(R.drawable.ic_currency)
            }

            Icon(
                painter = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = amenity,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}