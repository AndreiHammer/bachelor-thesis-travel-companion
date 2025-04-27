package eu.ase.travelcompanionapp.booking.presentation.bookinghistory

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.booking.presentation.bookinghistory.components.BookingTabContent
import eu.ase.travelcompanionapp.booking.presentation.bookinghistory.components.BookingTabs
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun BookingHistoryScreen(
    navController: NavController,
    viewModel: BookingHistoryViewModel = koinViewModel { parametersOf(navController) },
) {
    val state by viewModel.state.collectAsState()

    BackHandler {
        viewModel.handleAction(BookingHistoryAction.OnBackClick)
    }

    val activeBookingsListState = rememberLazyListState()
    val pastBookingsListState = rememberLazyListState()
    val pagerState = rememberPagerState(initialPage = state.selectedTabIndex)
    
    LaunchedEffect(state.selectedTabIndex) {
        if (pagerState.currentPage != state.selectedTabIndex) {
            pagerState.animateScrollToPage(state.selectedTabIndex)
        }
    }
    
    LaunchedEffect(pagerState.currentPage) {
        if (state.selectedTabIndex != pagerState.currentPage) {
            viewModel.handleAction(BookingHistoryAction.OnTabSelected(pagerState.currentPage))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.booking_history)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleAction(BookingHistoryAction.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = stringResource(R.string.loading_your_bookings),
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }

                state.error != null && state.bookings.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.error ?: stringResource(R.string.no_bookings_found),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        BookingTabs(
                            selectedTabIndex = state.selectedTabIndex,
                            onTabSelected = { index ->
                                viewModel.handleAction(BookingHistoryAction.OnTabSelected(index))
                            }
                        )
                        
                        HorizontalDivider()
                        
                        HorizontalPager(
                            count = 2,
                            state = pagerState,
                            modifier = Modifier.weight(1f)
                        ) { page ->
                            when (page) {
                                0 -> {
                                    BookingTabContent(
                                        bookings = state.activeBookings,
                                        emptyMessage = stringResource(R.string.no_active_reservations),
                                        listState = activeBookingsListState,
                                        onBookingClick = { booking, hotel ->
                                            viewModel.handleAction(BookingHistoryAction.OnBookingClick(booking, hotel))
                                        },
                                        createHotelFromBooking = viewModel::createHotelFromBooking
                                    )
                                }
                                1 -> {
                                    BookingTabContent(
                                        bookings = state.pastBookings,
                                        emptyMessage = stringResource(R.string.no_past_reservations),
                                        listState = pastBookingsListState,
                                        onBookingClick = { booking, hotel ->
                                            viewModel.handleAction(BookingHistoryAction.OnBookingClick(booking, hotel))
                                        },
                                        createHotelFromBooking = viewModel::createHotelFromBooking
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
