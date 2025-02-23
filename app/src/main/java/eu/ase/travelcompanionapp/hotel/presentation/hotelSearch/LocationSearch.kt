package eu.ase.travelcompanionapp.hotel.presentation.hotelSearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationOn
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
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.custom.AutoCompleteTextField
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.filters.CityFilterSearch

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

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name_2)) },
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
                }
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

            CityFilterSearch(
                city = city.value,
                onSearchClick = { selectedCity, selectedAmenities, selectedRatings ->
                    onAction(LocationSearchAction.OnSearchClick(selectedCity, selectedAmenities, selectedRatings))
                },
                onRatingSelected = onRatingSelected,
                onAmenitiesSelected = onAmenitiesSelected
            )
        }
    }
}
