package eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.custom

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.R

@Composable
fun RatingChipGroup(
    selectedHotelRating: Set<Int> = mutableSetOf(),
    onSelectedChanged: (MutableSet<Int>) -> Unit = {}
) {
    val hotelRatings:List<Int> = listOf(1,2,3,4,5)
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.padding(12.dp)) {
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(vertical = 8.dp)
        ) {
            hotelRatings.forEach { rating ->
                FilterChip(
                    onClick = {
                        val updatedSelectedHotelRating = selectedHotelRating.toMutableSet()
                        if (updatedSelectedHotelRating.contains(rating)) {
                            updatedSelectedHotelRating.remove(rating)
                        } else {
                            updatedSelectedHotelRating.add(rating)
                        }
                        onSelectedChanged(updatedSelectedHotelRating)
                    },
                    label = {
                        Text(
                            "$rating ${if (rating == 1) stringResource(R.string.star) else stringResource(R.string.stars)}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (selectedHotelRating.contains(rating)) 
                                    FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    },
                    selected = selectedHotelRating.contains(rating),
                    leadingIcon = {
                        Icon(
                            imageVector = if (selectedHotelRating.contains(rating)) 
                                Icons.Filled.Done else Icons.Filled.Star,
                            contentDescription = stringResource(
                                if (selectedHotelRating.contains(rating)) 
                                    R.string.done_icon else R.string.star
                            ),
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                            tint = if (selectedHotelRating.contains(rating))
                                MaterialTheme.colorScheme.primary
                            else
                                getStarColorForRating(rating)
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(end = 8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        labelColor = MaterialTheme.colorScheme.onSurface,
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    elevation = FilterChipDefaults.filterChipElevation(
                        elevation = 2.dp,
                        pressedElevation = 4.dp
                    )
                )
            }
        }
    }
}

@Composable
fun AmenitiesChipGroup(
    selectedHotelAmenities: Set<String> = mutableSetOf(),
    onSelectedChanged: (MutableSet<String>) -> Unit = {}
) {
    val hotelAmenities = stringArrayResource(id = R.array.hotel_amenities).toList()
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.padding(12.dp)) {
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(vertical = 8.dp)
        ) {
            hotelAmenities.forEach { amenity ->
                FilterChip(
                    onClick = {
                        val updatedSelectedAmenities = selectedHotelAmenities.toMutableSet()
                        if (updatedSelectedAmenities.contains(amenity)) {
                            updatedSelectedAmenities.remove(amenity)
                        } else {
                            updatedSelectedAmenities.add(amenity)
                        }
                        onSelectedChanged(updatedSelectedAmenities)
                    },
                    label = {
                        Text(
                            amenity,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (selectedHotelAmenities.contains(amenity)) 
                                    FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    },
                    selected = selectedHotelAmenities.contains(amenity),
                    leadingIcon = if (selectedHotelAmenities.contains(amenity)) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = stringResource(R.string.done_icon),
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        labelColor = MaterialTheme.colorScheme.onSurface,
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    elevation = FilterChipDefaults.filterChipElevation(
                        elevation = 2.dp,
                        pressedElevation = 4.dp
                    )
                )
            }
        }
    }
}

@Composable
private fun getStarColorForRating(rating: Int): Color {
    return when (rating) {
        1 -> Color(0xFFBDBDBD) // Gray
        2 -> Color(0xFFFFD54F) // Light yellow
        3 -> Color(0xFFFFCA28) // Yellow
        4 -> Color(0xFFFFB300) // Amber
        5 -> Color(0xFFFFA000) // Dark amber
        else -> MaterialTheme.colorScheme.primary
    }
}