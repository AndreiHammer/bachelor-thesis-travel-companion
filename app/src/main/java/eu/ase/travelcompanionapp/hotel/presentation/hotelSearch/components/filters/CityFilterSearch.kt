package eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.filters

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.core.domain.utils.DateUtils
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.LocationSearchAction
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.custom.AmenitiesChipGroup
import eu.ase.travelcompanionapp.core.presentation.DatePickerWithDialog
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.custom.GuestSelector
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.custom.RatingChipGroup

@Composable
fun CityFilterSearch(
    city: String,
    onAction: (LocationSearchAction) -> Unit
) {
    val selectedRatings = remember { mutableStateOf(setOf<Int>()) }
    val selectedAmenities = remember { mutableStateOf(setOf<String>()) }
    val checkInDate = remember { mutableStateOf("") }
    val checkOutDate = remember { mutableStateOf("") }
    val adults = remember { mutableIntStateOf(1) }

    val dateUtils = remember { DateUtils() }
    val today = remember { System.currentTimeMillis() }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    
                    Text(
                        text = stringResource(R.string.select_dates),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DatePickerWithDialog(
                        label = stringResource(R.string.check_in_date),
                        selectedDate = checkInDate.value,
                        minDate = today,
                        onDateSelected = { dateString ->
                            checkInDate.value = dateString
                            if (checkOutDate.value.isNotEmpty()) {
                                val checkInDateTime = dateUtils.parseDisplayDate(dateString)
                                val checkOutDateTime = dateUtils.parseDisplayDate(checkOutDate.value)
                                if (checkOutDateTime != null && checkInDateTime != null &&
                                    !checkOutDateTime.isAfter(checkInDateTime)) {
                                    checkOutDate.value = ""
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    DatePickerWithDialog(
                        label = stringResource(R.string.check_out_date),
                        selectedDate = checkOutDate.value,
                        minDate = if (checkInDate.value.isNotEmpty()) {
                            val checkInDateTime = dateUtils.parseDisplayDate(checkInDate.value)
                            checkInDateTime?.plusDays(1)?.toInstant()?.toEpochMilli() ?: today
                        } else {
                            dateUtils.convertMillisToLocalDate(today).plusDays(1).toInstant().toEpochMilli()
                        },
                        onDateSelected = { dateString ->
                            checkOutDate.value = dateString
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        SectionTitle(title = stringResource(R.string.select_number_of_adults))
        
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GuestSelector(
                    value = adults.intValue,
                    onValueChange = { adults.intValue = it },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        SectionTitle(title = stringResource(R.string.select_rating))
        
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            RatingChipGroup(
                selectedHotelRating = selectedRatings.value,
                onSelectedChanged = { updatedRatings ->
                    selectedRatings.value = updatedRatings
                    onAction(LocationSearchAction.OnRatingSelected(updatedRatings))
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        SectionTitle(title = stringResource(R.string.select_amenities))
        
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            AmenitiesChipGroup(
                selectedHotelAmenities = selectedAmenities.value,
                onSelectedChanged = { updatedAmenities ->
                    selectedAmenities.value = updatedAmenities
                    onAction(LocationSearchAction.OnAmenitiesSelected(updatedAmenities))
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onAction(LocationSearchAction.OnSearchClick(city,
                    selectedAmenities.value,
                    selectedRatings.value))
                onAction(LocationSearchAction.OnOfferDetailsSet(checkInDate.value, checkOutDate.value, adults.intValue))
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = stringResource(R.string.search_hotels_in, city),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold
        ),
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}