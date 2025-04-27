package eu.ase.travelcompanionapp.booking.presentation.bookinghistory.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.booking.domain.models.BookingInfo
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel

@Composable
fun BookingTabContent(
    bookings: List<BookingInfo>,
    emptyMessage: String,
    listState: LazyListState,
    onBookingClick: (BookingInfo, Hotel) -> Unit,
    createHotelFromBooking: (BookingInfo) -> Hotel,
    modifier: Modifier = Modifier
) {
    if (bookings.isEmpty()) {
        EmptyBookingState(message = emptyMessage, modifier = modifier)
    } else {
        BookingList(
            bookings = bookings,
            listState = listState,
            onBookingClick = onBookingClick,
            createHotelFromBooking = createHotelFromBooking,
            modifier = modifier
        )
    }
}

@Composable
private fun EmptyBookingState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BookingList(
    bookings: List<BookingInfo>,
    listState: LazyListState,
    onBookingClick: (BookingInfo, Hotel) -> Unit,
    createHotelFromBooking: (BookingInfo) -> Hotel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        state = listState
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }
        
        items(
            items = bookings,
            key = { booking -> booking.bookingReference }
        ) { booking ->
            BookingHistoryItem(
                booking = booking,
                onClick = {
                    val hotel = createHotelFromBooking(booking)
                    onBookingClick(booking, hotel)
                }
            )
        }
        
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
} 