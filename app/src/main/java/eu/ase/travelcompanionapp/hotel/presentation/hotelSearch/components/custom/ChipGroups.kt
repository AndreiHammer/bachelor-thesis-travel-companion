package eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.custom

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.R

@Composable
fun RatingChipGroup(
    selectedHotelRating: Set<Int> = mutableSetOf(),
    onSelectedChanged: (MutableSet<Int>) -> Unit = {}
) {
    val hotelRatings:List<Int> = listOf(1,2,3,4,5)

    Column(modifier = Modifier.padding(8.dp)) {
        LazyRow {
            items(hotelRatings) { rating ->
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
                        Text(rating.toString())
                    },
                    selected = selectedHotelRating.contains(rating),
                    leadingIcon = if (selectedHotelRating.contains(rating)) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = stringResource(R.string.done_icon),
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    }
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

    Column(modifier = Modifier.padding(8.dp)) {
        LazyRow {
            items(hotelAmenities) { amenity ->
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
                        Text(amenity)
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
                    }
                )
            }
        }
    }
}