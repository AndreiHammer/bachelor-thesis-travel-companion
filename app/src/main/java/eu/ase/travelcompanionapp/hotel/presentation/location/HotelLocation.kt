package eu.ase.travelcompanionapp.hotel.presentation.location

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.ase.travelcompanionapp.core.presentation.BlurredAnimatedText
import eu.ase.travelcompanionapp.hotel.domain.Hotel
import eu.ase.travelcompanionapp.hotel.presentation.location.components.HotelMap
import org.koin.androidx.compose.koinViewModel

@Composable
fun HotelLocationScreenRoot(
    hotel: Hotel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    HotelLocationScreen(
        hotel = hotel,
        modifier = modifier,
        onAction = { action ->
            when (action) {
                HotelLocationAction.OnBackClick -> onBackClick()
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelLocationScreen(
    hotel: Hotel,
    modifier: Modifier = Modifier,
    onAction: (HotelLocationAction) -> Unit
) {
    val viewModel: HotelLocationViewModel = koinViewModel()

    val hotelState = viewModel.hotelState.collectAsStateWithLifecycle()

    LaunchedEffect(hotel.name, hotel.countryCode) {
        viewModel.getHotelDetails(hotel.name, hotel.countryCode)
    }


    val isLoading = hotelState.value.photos.isEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text =hotel.name) },
                navigationIcon = {
                    IconButton(onClick = { onAction(HotelLocationAction.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
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
                    BlurredAnimatedText(text = "Loading details...")
                }
            } else {
                HotelMap(
                    modifier = Modifier.fillMaxSize(),
                    hotelState = hotelState.value
                )
            }
        }
    }
}


