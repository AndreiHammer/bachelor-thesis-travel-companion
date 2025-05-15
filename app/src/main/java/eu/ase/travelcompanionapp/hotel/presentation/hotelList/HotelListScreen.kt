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
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.components.HotelList
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.components.HotelListScreenError
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.ui.input.nestedscroll.nestedScroll
import coil3.Bitmap
import eu.ase.travelcompanionapp.hotel.domain.model.HotelPrice
import eu.ase.travelcompanionapp.ui.CompanionTopAppBar


@Composable
fun HotelListScreenRoot(
    viewModel: HotelListViewModel = koinViewModel(),
    selectedCity: String? = null,
    latitude: Double? = null,
    longitude: Double? = null,
    radius: Int? = null,
    amenities: Set<String>? = emptySet(),
    ratings: Set<Int>? = emptySet()
) {
    val state by viewModel.hotelState.collectAsStateWithLifecycle()
    val hotelPrices by viewModel.hotelPrices.collectAsStateWithLifecycle()

    // THIS WOULD BE USED WHEN THE APP IS FINISHED, IN TESTING IT IS TAKEN OUT DUE TO API COSTS
    /*val hotelImages by viewModel.hotelImages.collectAsStateWithLifecycle()

    LaunchedEffect(state.hotelItems) {
        viewModel.loadHotelImages()
    }*/

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
        state.errorMessage != null || state.hotelItems.isEmpty() && !state.isLoading -> {
            HotelListScreenError(
                errorMessage = state.errorMessage ?: stringResource(R.string.no_hotels_found),
                onAction = { action ->
                    viewModel.handleAction(action)
                }
            )
        }
        else -> {
            HotelListScreen(
                state = state,
                hotelPrices = hotelPrices,
                selectedCity = selectedCity,
                //hotelImages = hotelImages,
                onAction = { action ->
                    viewModel.handleAction(action)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HotelListScreen(
    state: HotelListViewModel.HotelListState,
    hotelPrices: Map<String, HotelPrice>,
    selectedCity: String?,
    onAction: (HotelListAction) -> Unit,
    hotelImages: Map<String, Bitmap?> = emptyMap(),
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            val title = if (selectedCity != null)
                stringResource(R.string.hotels_list, selectedCity)
            else
                stringResource(R.string.hotels_list_by_location)
                
            val subtitle = if (state.hotelItems.isNotEmpty()) {
                stringResource(R.string.hotels_found, state.hotelItems.size)
            } else null
            
            CompanionTopAppBar(
                title = title,
                subtitle = subtitle,
                onNavigationClick = { onAction(HotelListAction.OnBackClick) },
                scrollBehavior = scrollBehavior
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
                            hotelItems = state.hotelItems,
                            hotelPrices = hotelPrices,
                            hotelImages = hotelImages,
                            onHotelClick = { hotel ->
                                onAction(HotelListAction.OnHotelClick(hotel))
                            },
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
