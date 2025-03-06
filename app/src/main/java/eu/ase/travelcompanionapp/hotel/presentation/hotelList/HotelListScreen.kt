package eu.ase.travelcompanionapp.hotel.presentation.hotelList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.core.presentation.BlurredAnimatedText
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.components.HotelList
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.components.HotelListScreenError
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface


@Composable
fun HotelListScreenRoot(
    viewModel: HotelListViewModel = koinViewModel(),
    onHotelClick: (Hotel) -> Unit,
    onBackClick: () -> Unit,
    selectedCity: String? = null,
    latitude: Double? = null,
    longitude: Double? = null,
    radius: Int? = null,
    amenities: Set<String>? = emptySet(),
    ratings: Set<Int>? = emptySet()
) {
    val state by viewModel.hotelState.collectAsStateWithLifecycle()

    LaunchedEffect(selectedCity, latitude, longitude, radius, amenities, ratings) {
        if (selectedCity != null) {
            viewModel.getHotelListByCity(
                city = selectedCity,
                amenities = amenities?.joinToString(",") ?: "",
                rating = if (ratings.isNullOrEmpty()) "" else ratings.joinToString(",")
            )
        } else if (latitude != null && longitude != null && radius != null) {
            viewModel.getHotelListByLocation(
                latitude = latitude,
                longitude = longitude,
                radius = radius,
                amenities = amenities?.joinToString(",") ?: "",
                rating = if (ratings.isNullOrEmpty()) "" else ratings.joinToString(",")
            )
        }
    }

    when {
        state.errorMessage != null || state.hotels.isEmpty() && !state.isLoading -> {
            HotelListScreenError(
                errorMessage = state.errorMessage ?: stringResource(R.string.no_hotels_found),
                onAction = { action ->
                    when (action) {
                        HotelListAction.OnBackClick -> onBackClick()
                        else -> {}
                    }
                }
            )
        }
        else -> {
            HotelListScreen(
                state = state,
                selectedCity = selectedCity,
                onHotelClick = onHotelClick,
                onBackClick = onBackClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HotelListScreen(
    state: HotelListViewModel.HotelListState,
    selectedCity: String?,
    onHotelClick: (Hotel) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (selectedCity != null) 
                                stringResource(R.string.hotels_list, selectedCity)
                            else 
                                stringResource(R.string.hotels_list_by_location)
                        )
                        if (state.hotels.isNotEmpty()) {
                            Text(
                                text = stringResource(
                                    R.string.hotels_found,
                                    state.hotels.size
                                ),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        BlurredAnimatedText(
                            text = stringResource(R.string.loading_hotels)
                        )
                    }
                }
                else -> {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        HotelList(
                            hotels = state.hotels,
                            onHotelClick = onHotelClick,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}
