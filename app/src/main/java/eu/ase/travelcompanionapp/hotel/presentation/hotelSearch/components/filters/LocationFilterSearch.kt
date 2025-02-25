package eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.filters

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.LocationSearchAction
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.custom.AmenitiesChipGroup
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.custom.DatePickerWithDialog
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.custom.RatingChipGroup

@Composable
fun LocationFilterSearch(
    range: Int,
    initialSelectedRatings: Set<Int>,
    initialSelectedAmenities: Set<String>,
    initialCheckInDate: String,
    initialCheckOutDate: String,
    initialAdults: Int,
    onAction: (LocationSearchAction) -> Unit,
    onSearchClick: (Set<String>, Set<Int>, String, String, Int) -> Unit,
    onRangeChange: (Int) -> Unit
) {
    val selectedRatings = remember { mutableStateOf(initialSelectedRatings) }
    val selectedAmenities = remember { mutableStateOf(initialSelectedAmenities) }
    val checkInDate = remember { mutableStateOf(initialCheckInDate) }
    val checkOutDate = remember { mutableStateOf(initialCheckOutDate) }
    val adults = remember { mutableIntStateOf(initialAdults) }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = stringResource(R.string.select_search_radius), style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = range.toFloat(),
            onValueChange = { onRangeChange(it.toInt()) },
            valueRange = 1f..50f,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(R.string.range_km, range),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))


        Text(text = stringResource(R.string.select_dates), style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DatePickerWithDialog(
                label = stringResource(R.string.check_in_date),
                selectedDate = checkInDate.value,
                onDateSelected = { dateString -> checkInDate.value = dateString },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            DatePickerWithDialog(
                label = stringResource(R.string.check_out_date),
                selectedDate = checkOutDate.value,
                onDateSelected = { dateString -> checkOutDate.value = dateString },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(R.string.select_number_of_adults), style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = adults.intValue.toFloat(),
            onValueChange = { adults.intValue = it.toInt() },
            valueRange = 1f..10f,
            steps = 8,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "${adults.intValue} ${if (adults.intValue == 1) stringResource(R.string.adult) else stringResource(R.string.adults)}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(R.string.select_rating), style = MaterialTheme.typography.bodyMedium)
        RatingChipGroup(
            selectedHotelRating = selectedRatings.value,
            onSelectedChanged = { updatedRatings ->
                selectedRatings.value = updatedRatings
                onAction(LocationSearchAction.OnRatingSelected(updatedRatings))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(R.string.select_amenities), style = MaterialTheme.typography.bodyMedium)
        AmenitiesChipGroup(
            selectedHotelAmenities = selectedAmenities.value,
            onSelectedChanged = { updatedAmenities ->
                selectedAmenities.value = updatedAmenities
                onAction(LocationSearchAction.OnAmenitiesSelected(updatedAmenities))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onSearchClick(
                    selectedAmenities.value,
                    selectedRatings.value,
                    checkInDate.value,
                    checkOutDate.value,
                    adults.intValue
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.search))
        }
    }
}
