package eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.core.presentation.BlurredAnimatedText
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.HotelLocationAction
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.test.components.HotelMapTest
import org.koin.androidx.compose.koinViewModel

@Composable
fun HotelLocationTestScreenRoot(
    hotel: Hotel,
    modifier: Modifier = Modifier,
    checkInDate: String,
    checkOutDate: String,
    adults: Int,
    viewModel: HotelLocationTestViewModel = koinViewModel()
) {
    HotelLocationTestScreen(
        hotel = hotel,
        modifier = modifier,
        viewModel = viewModel,
        checkInDate = checkInDate,
        checkOutDate = checkOutDate,
        adults = adults
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelLocationTestScreen(
    hotel: Hotel,
    modifier: Modifier = Modifier,
    viewModel: HotelLocationTestViewModel,
    checkInDate: String,
    checkOutDate: String,
    adults: Int
) {
    val hotelState = viewModel.hotelState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getHotelDetails()
    }

    val isLoading = hotelState.value.isLoading

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = hotel.name)
                        Text(
                            text = " (Test Mode)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.handleAction(HotelLocationAction.OnBackClick, hotel)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.handleAction(HotelLocationAction.OnFavouriteClick(checkInDate, checkOutDate, adults), hotel)
                        }
                    ) {
                        Icon(
                            imageVector = if(hotelState.value.isFavourite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = stringResource(R.string.favourite),
                            tint = Color.Red
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    BlurredAnimatedText(text = stringResource(R.string.loading_details))
                }
            } else {
                HotelMapTest(
                    modifier = Modifier.fillMaxSize(),
                    hotelState = hotelState.value,
                    onNavigateToOffers = {
                        viewModel.handleAction(
                            HotelLocationAction.OnViewOfferClick(checkInDate, checkOutDate, adults),
                            hotel
                        )
                    }
                )
            }
        }
    }
}