package eu.ase.travelcompanionapp.recommendation.presentation.destinations.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.core.domain.utils.DateUtils
import eu.ase.travelcompanionapp.core.presentation.DatePickerWithDialog
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.custom.AmenitiesChipGroup
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.custom.GuestSelector
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.custom.RatingChipGroup

@Composable
fun HotelSearchFilterDialog(
    cityName: String,
    isVisible: Boolean,
    initialCheckInDate: String = "",
    initialCheckOutDate: String = "",
    initialAdults: Int = 1,
    initialRatings: Set<Int> = emptySet(),
    initialAmenities: Set<String> = emptySet(),
    onDismiss: () -> Unit,
    onSearchClick: (
        checkInDate: String,
        checkOutDate: String,
        adults: Int,
        ratings: Set<Int>,
        amenities: Set<String>
    ) -> Unit
) {
    if (!isVisible) return

    val selectedRatings = remember(initialRatings) {
        mutableStateOf(initialRatings) 
    }
    val selectedAmenities = remember(initialAmenities) {
        mutableStateOf(initialAmenities) 
    }
    val checkInDate = remember(initialCheckInDate) {
        mutableStateOf(initialCheckInDate) 
    }
    val checkOutDate = remember(initialCheckOutDate) {
        mutableStateOf(initialCheckOutDate) 
    }
    val adults = remember(initialAdults) {
        mutableIntStateOf(initialAdults) 
    }

    val dateUtils = remember { DateUtils() }
    val today = remember { System.currentTimeMillis() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Dialog Title
                Text(
                    text = stringResource(R.string.search_hotels_in_city, cityName),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Date Selection Section
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

                // Guests Section
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
                            onValueChange = { newValue ->
                                adults.intValue = newValue
                            },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Rating Section
                SectionTitle(title = stringResource(R.string.select_rating))
                
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    RatingChipGroup(
                        selectedHotelRating = selectedRatings.value,
                        onSelectedChanged = { updatedRatings ->
                            selectedRatings.value = updatedRatings
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Amenities Section
                SectionTitle(title = stringResource(R.string.select_amenities))
                
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    AmenitiesChipGroup(
                        selectedHotelAmenities = selectedAmenities.value,
                        onSelectedChanged = { updatedAmenities ->
                            selectedAmenities.value = updatedAmenities
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }

                    Button(
                        onClick = {
                            onSearchClick(
                                checkInDate.value,
                                checkOutDate.value,
                                adults.intValue,
                                selectedRatings.value,
                                selectedAmenities.value
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.search),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
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
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(vertical = 8.dp)
    )
} 