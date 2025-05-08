package eu.ase.travelcompanionapp.touristattractions.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.touristattractions.domain.model.TouristAttraction

@Composable
fun TouristAttractionsSection(
    attractions: List<TouristAttraction>,
    isLoading: Boolean = false,
    onSeeAllClick: ((Double, Double) -> Unit)? = null,
    onAttractionClick: ((TouristAttraction) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            SectionHeader(
                attractions = attractions,
                onSeeAllClick = onSeeAllClick
            )
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AttractionLoadingState(
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                attractions.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.no_tourist_attractions_found_nearby),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                else -> {
                    AttractionsList(
                        attractions = attractions,
                        onAttractionClick = onAttractionClick
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    attractions: List<TouristAttraction>,
    onSeeAllClick: ((Double, Double) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.nearby_tourist_attractions),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        if (attractions.isNotEmpty() && onSeeAllClick != null) {
            val firstAttraction = attractions.first()
            val lat = firstAttraction.latitude
            val lng = firstAttraction.longitude
            
            if (lat != null && lng != null) {
                TextButton(
                    onClick = { onSeeAllClick(lat, lng) }
                ) {
                    Text(
                        text = stringResource(R.string.see_all),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun AttractionsList(
    attractions: List<TouristAttraction>,
    onAttractionClick: ((TouristAttraction) -> Unit)?,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        modifier = modifier
    ) {
        items(attractions) { attraction ->
            AttractionCard(
                attraction = attraction,
                onClick = { onAttractionClick?.invoke(attraction) }
            )
        }
    }
} 