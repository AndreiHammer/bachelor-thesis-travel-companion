package eu.ase.travelcompanionapp.hotel.presentation.hotelFavourites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.core.presentation.BlurredAnimatedText
import eu.ase.travelcompanionapp.hotel.presentation.hotelFavourites.components.EmptyFavouritesContent
import eu.ase.travelcompanionapp.hotel.presentation.hotelFavourites.components.FavouriteHotelsList
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelFavouriteScreen(
    viewModel: HotelFavouriteViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val state = viewModel.hotelState.collectAsStateWithLifecycle()
    val hotelPrices = viewModel.hotelPrices.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getHotelFavourites()
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.favourite_hotels)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleAction(HotelFavouriteAction.OnBackClick) }) {
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
            if (state.value.isLoading) {
                BlurredAnimatedText(stringResource(R.string.loading_hotels))
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
                        }
                    )
                }
            }
        }
    }
}

