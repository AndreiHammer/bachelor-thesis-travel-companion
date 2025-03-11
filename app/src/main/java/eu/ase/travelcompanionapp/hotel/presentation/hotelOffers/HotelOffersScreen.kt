package eu.ase.travelcompanionapp.hotel.presentation.hotelOffers

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.core.presentation.BlurredAnimatedText
import eu.ase.travelcompanionapp.hotel.presentation.hotelOffers.components.HotelOffersSection
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelOffersScreen(
    hotelId: String,
    checkInDate: String,
    checkOutDate: String,
    adults: Int,
    viewModel: HotelOffersViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(hotelId) {
        viewModel.getHotelOffers(hotelId, checkInDate, checkOutDate, adults)
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.hotel_offers)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleAction(HotelOffersAction.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    BlurredAnimatedText(text = stringResource(R.string.loading_offers))
                }
            } else {
                HotelOffersSection(
                    hotelOffers = state.offers,
                    errorMessage = state.error ?: stringResource(R.string.no_offers_available),
                    onBookNow = {
                        viewModel.handleAction(HotelOffersAction.OnBookNow)
                    }
                )
            }
        }
    }
}