package eu.ase.travelcompanionapp.hotel.presentation.hotelOffers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.core.presentation.BlurredAnimatedText
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.components.BookingDetailsDialog
import eu.ase.travelcompanionapp.hotel.presentation.hotelOffers.components.HotelOffersSection
import eu.ase.travelcompanionapp.ui.CompanionTopAppBar
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
    val convertedPrices by viewModel.convertedPrices.collectAsStateWithLifecycle()
    val showModifyDialog by viewModel.showModifyDialog.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(hotelId) {
        viewModel.getHotelOffers(hotelId, checkInDate, checkOutDate, adults)
    }
    
    if (showModifyDialog) {
        val currentDetails = viewModel.getCurrentBookingDetails()
        BookingDetailsDialog(
            initialBookingDetails = currentDetails,
            title = stringResource(R.string.modify_booking_details),
            description = stringResource(R.string.modify_booking_details_description),
            confirmButtonText = stringResource(R.string.view_offers),
            onDismiss = { viewModel.handleAction(HotelOffersAction.OnDismissDialog) },
            onConfirm = { newCheckInDate, newCheckOutDate, newAdults ->
                viewModel.handleAction(
                    HotelOffersAction.OnModifyBookingDetails(
                        hotelId = hotelId,
                        checkInDate = newCheckInDate,
                        checkOutDate = newCheckOutDate,
                        adults = newAdults
                    )
                )
            }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CompanionTopAppBar(
                title = stringResource(R.string.hotel_offers),
                onNavigationClick = { viewModel.handleAction(HotelOffersAction.OnBackClick) },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(
                        onClick = { viewModel.handleAction(HotelOffersAction.OnShowModifyDialog) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = stringResource(R.string.modify_dates)
                        )
                    }
                }
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
                    convertedPrices = convertedPrices,
                    onBookNow = { offer ->
                        viewModel.handleAction(HotelOffersAction.OnBookNow(offer))
                    }
                )
            }
        }
    }
}