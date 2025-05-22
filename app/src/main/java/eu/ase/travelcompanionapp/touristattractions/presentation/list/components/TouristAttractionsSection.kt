package eu.ase.travelcompanionapp.touristattractions.presentation.list.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
        )
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
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AttractionLoadingState(
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                attractions.isEmpty() -> {
                    EmptyAttractionsState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
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
private fun EmptyAttractionsState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_attractions_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.no_tourist_attractions_found_nearby),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
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
            .padding(bottom = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                ),
                modifier = Modifier.size(36.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_attractions_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.nearby_tourist_attractions),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        
        if (attractions.isNotEmpty() && onSeeAllClick != null) {
            val firstAttraction = attractions.first()
            val lat = firstAttraction.latitude
            val lng = firstAttraction.longitude
            
            if (lat != null && lng != null) {
                OutlinedButton(
                    onClick = { onSeeAllClick(lat, lng) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.see_all),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
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
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp),
        modifier = modifier
    ) {
        items(attractions.take(6)) { attraction ->
            AttractionCard(
                attraction = attraction,
                onClick = { onAttractionClick?.invoke(attraction) },
                modifier = Modifier
                    .width(220.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
} 