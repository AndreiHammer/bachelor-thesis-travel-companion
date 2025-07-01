package eu.ase.travelcompanionapp.hotel.presentation.hotelFavourites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.hotel.presentation.hotelFavourites.components.EmptyFavouritesContent
import eu.ase.travelcompanionapp.hotel.presentation.hotelFavourites.components.FavouriteHotelsList
import eu.ase.travelcompanionapp.ui.CompanionTopAppBar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelFavouriteScreen(
    viewModel: HotelFavouriteViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val state = viewModel.hotelState.collectAsStateWithLifecycle()
    val hotelPrices = viewModel.hotelPrices.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val hotelImages by viewModel.hotelImages.collectAsStateWithLifecycle()

    LaunchedEffect(state.value.hotelsWithDetails) {
        if (state.value.hotelsWithDetails.isNotEmpty()) {
            viewModel.loadHotelImages()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getHotelFavourites()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CompanionTopAppBar(
                title = stringResource(R.string.favourite_hotels),
                onNavigationClick = { viewModel.handleAction(HotelFavouriteAction.OnBackClick) },
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
            if (state.value.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp)
                )
            } else if (state.value.errorMessage != null || state.value.hotelsWithDetails.isEmpty()) {
                EmptyFavouritesContent()
            } else {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FavouriteHotelsList(
                        hotels = state.value.hotelsWithDetails,
                        hotelPrices = hotelPrices.value,
                        onHotelClick = { hotel ->
                            viewModel.handleAction(HotelFavouriteAction.OnHotelClick(hotel))
                        },
                        onDeleteClick = { hotelId ->
                            viewModel.handleAction(HotelFavouriteAction.OnRemoveFavourite(hotelId))
                        },
                        hotelImages = hotelImages
                    )
                }
            }
        }
    }
}

