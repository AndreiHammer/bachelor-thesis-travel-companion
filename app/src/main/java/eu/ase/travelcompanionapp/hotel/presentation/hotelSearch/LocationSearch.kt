package eu.ase.travelcompanionapp.hotel.presentation.hotelSearch

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.autocomplete.AutoCompleteTextField
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.custom.NearbyAccomodationsCard
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.filters.CityFilterSearch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun LocationSearchScreen(
    modifier: Modifier = Modifier,
    onAction: (LocationSearchAction) -> Unit,
    sharedViewModel: SharedViewModel
) {
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val context = LocalContext.current
    val viewModel = koinViewModel<LocationSearchViewModel>()

    val isWaitingForPermission = remember { mutableStateOf(false) }


    LaunchedEffect(locationPermissionState.status) {
        if (isWaitingForPermission.value && locationPermissionState.status is PermissionStatus.Granted) {
            isWaitingForPermission.value = false
            viewModel.performNearbySearch(
                context = context,
                sharedViewModel = sharedViewModel,
                radius = 20,
                onResult = { success ->
                    if (success) {
                        onAction(LocationSearchAction.OnNearbySearchClick)
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.unable_to_get_your_location_please_check_your_location_settings),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
    }

    val city = remember { mutableStateOf("") }

    onAction(LocationSearchAction.OnRatingSelected(emptySet()))
    onAction(LocationSearchAction.OnAmenitiesSelected(emptySet()))
    onAction(LocationSearchAction.OnOfferDetailsSet("", "", 0))

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
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            NearbyAccomodationsCard(
                onNearbySearchClick = {
                    when (locationPermissionState.status) {
                        is PermissionStatus.Granted -> {
                            viewModel.performNearbySearch(
                                context = context,
                                sharedViewModel = sharedViewModel,
                                radius = 20,
                                onResult = { success ->
                                    if (success) {
                                        onAction(LocationSearchAction.OnNearbySearchClick)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.unable_to_get_your_location_please_check_your_location_settings),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            )
                        }
                        is PermissionStatus.Denied -> {
                            isWaitingForPermission.value = true
                            locationPermissionState.launchPermissionRequest()
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.filter_your_preferences), style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.select_destination), style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            AutoCompleteTextField(
                onCitySelected = { selectedCity ->
                    city.value = selectedCity
                    onAction(LocationSearchAction.OnCitySelected(selectedCity))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn {
                    item {
                        CityFilterSearch(
                            city = city.value,
                            onAction = onAction
                        )
                    }
                }
            }
        }
    }
}
