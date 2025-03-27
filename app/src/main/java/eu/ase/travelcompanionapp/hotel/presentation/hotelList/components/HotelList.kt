package eu.ase.travelcompanionapp.hotel.presentation.hotelList.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.domain.model.HotelPrice
import eu.ase.travelcompanionapp.hotel.domain.model.HotelWithBookingDetails

@Composable
fun HotelList(
    hotelItems: List<HotelWithBookingDetails>,
    onHotelClick: (Hotel) -> Unit,
    hotelPrices: Map<String, HotelPrice> = emptyMap(),
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(hotelItems){ hotelItem ->
            HotelItem(
                hotel = hotelItem.hotel, 
                onClick = { onHotelClick(hotelItem.hotel) },
                hotelPrice = hotelPrices[hotelItem.hotel.hotelId],
                bookingDetails = hotelItem.bookingDetails
            )
        }
    }
}