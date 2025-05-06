package eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.filters

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
    val scrollState = rememberScrollState()

    val dateUtils = remember { DateUtils() }
    val today = remember { System.currentTimeMillis() }

    Column(
        modifier = Modifier
            .padding(12.dp)
            .verticalScroll(scrollState)
    ) {
        FilterSection(
            icon = Icons.Default.LocationOn,
            title = stringResource(R.string.select_search_radius)
        ) {
            Slider(
                value = range.toFloat(),
                onValueChange = { onRangeChange(it.toInt()) },
                valueRange = 1f..50f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
            
            Text(
                text = stringResource(R.string.range_km, range),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        FilterSection(
            icon = Icons.Default.DateRange,
            title = stringResource(R.string.select_dates)
        ) {
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

                Spacer(modifier = Modifier.width(8.dp))

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

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        FilterSection(
            icon = Icons.Default.Person,
            title = stringResource(R.string.select_number_of_adults)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                GuestSelector(
                    value = adults.intValue,
                    onValueChange = { adults.intValue = it },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        SectionHeader(title = stringResource(R.string.select_rating))
        RatingChipGroup(
            selectedHotelRating = selectedRatings.value,
            onSelectedChanged = { updatedRatings ->
                selectedRatings.value = updatedRatings
                onAction(LocationSearchAction.OnRatingSelected(updatedRatings))
            }
        )

        SectionHeader(title = stringResource(R.string.select_amenities))
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
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
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
                text = stringResource(R.string.search),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun FilterSection(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 8.dp)
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}
