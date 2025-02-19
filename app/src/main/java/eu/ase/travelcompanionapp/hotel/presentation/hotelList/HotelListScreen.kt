package eu.ase.travelcompanionapp.hotel.presentation.hotelList

import androidx.compose.foundation.background
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
    ratings: Set<Int>? = emptySet(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.hotelState.collectAsStateWithLifecycle()

    if (selectedCity != null) {
        viewModel.getHotelListByCity(city = selectedCity, amenities = amenities?.joinToString(",") ?: "", rating = ratings?.joinToString(",") ?: "")
    } else if (latitude != null && longitude != null && radius != null) {
        viewModel.getHotelListByLocation(latitude = latitude, longitude = longitude, radius = radius, amenities = amenities?.joinToString(",") ?: "", rating = ratings?.joinToString(",") ?: "")
    }

    if (state.errorMessage != null) {
        HotelListScreenError(
            errorMessage = stringResource(R.string.invalid_city_error),
            onAction = { action ->
                if (action is HotelListAction.OnBackClick) {
                    onBackClick()
                }
            }
        )
    } else {
        HotelListScreen(
            state = state,
            selectedCity = selectedCity,
            onAction = { action ->
                when (action) {
                    is HotelListAction.OnHotelClick -> onHotelClick(action.hotel)
                    is HotelListAction.OnBackClick -> onBackClick()
                }
                viewModel.onAction(action)
            },
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelListScreen(
    modifier: Modifier = Modifier,
    state: HotelListViewModel.HotelListState,
    selectedCity: String?,
    onAction: (HotelListAction) -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = if (selectedCity != null) stringResource(R.string.hotels_list, selectedCity)
                        else stringResource(R.string.hotels_list_by_location))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = { onAction(HotelListAction.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    BlurredAnimatedText(text = stringResource(R.string.loading_hotels))
                }
            } else {
                HotelList(
                    hotels = state.hotels,
                    onHotelClick = { hotel ->
                        onAction(HotelListAction.OnHotelClick(hotel))
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}