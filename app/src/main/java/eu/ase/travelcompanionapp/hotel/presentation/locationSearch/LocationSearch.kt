package eu.ase.travelcompanionapp.hotel.presentation.locationSearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.hotel.presentation.locationSearch.components.AmenitiesChipGroup
import eu.ase.travelcompanionapp.hotel.presentation.locationSearch.components.AutoCompleteTextField
import eu.ase.travelcompanionapp.hotel.presentation.locationSearch.components.RatingChipGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchScreen(
    modifier: Modifier = Modifier,
    onAction: (LocationSearchAction) -> Unit,
    onRatingSelected: (Set<Int>) -> Unit,
    onCitySelected: (String) -> Unit,
    onAmenitiesSelected: (Set<String>) -> Unit,
) {
    val city = remember { mutableStateOf("") }
    val selectedRatings = remember { mutableStateOf(setOf<Int>()) }
    val selectedAmenities = remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.search)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = stringResource(R.string.user_profile),
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                onAction(LocationSearchAction.OnProfileClick)
                            },
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                },
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAction(LocationSearchAction.OnMapClick)
                }
            ) {
                Icon(imageVector = Icons.Filled.LocationOn, contentDescription = stringResource(R.string.map))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(text = stringResource(R.string.filter_your_preferences), style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.select_city), style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            AutoCompleteTextField(
                onCitySelected = { selectedCity ->
                    city.value = selectedCity
                    onCitySelected(selectedCity)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = stringResource(R.string.select_rating), style = MaterialTheme.typography.bodyMedium)

            RatingChipGroup(
                selectedHotelRating = selectedRatings.value,
                onSelectedChanged = { updatedRatings ->
                    selectedRatings.value = updatedRatings
                    onRatingSelected(updatedRatings)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = stringResource(R.string.select_amenities), style = MaterialTheme.typography.bodyMedium)

            AmenitiesChipGroup(
                selectedHotelAmenities = selectedAmenities.value,
                onSelectedChanged = { updatedAmenities ->
                    selectedAmenities.value = updatedAmenities
                    onAmenitiesSelected(updatedAmenities)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onAction(LocationSearchAction.OnSearchClick(city.value, selectedAmenities.value, selectedRatings.value))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.search_hotels_in, city.value))
            }
        }
    }
}
