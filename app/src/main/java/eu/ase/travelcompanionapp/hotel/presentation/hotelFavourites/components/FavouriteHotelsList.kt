package eu.ase.travelcompanionapp.hotel.presentation.hotelFavourites.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.Bitmap
import eu.ase.travelcompanionapp.hotel.domain.model.HotelPrice
import eu.ase.travelcompanionapp.hotel.domain.model.HotelWithBookingDetails

@Composable
fun FavouriteHotelsList(
    hotels: List<HotelWithBookingDetails>,
    hotelPrices: Map<String, HotelPrice>,
    hotelImages: Map<String, Bitmap?> = emptyMap(),
    onHotelClick: (hotel: eu.ase.travelcompanionapp.hotel.domain.model.Hotel) -> Unit,
    onDeleteClick: (hotelId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(
            items = hotels,
            key = { it.hotel.hotelId }
        ) { hotelWithDetails ->
            FavouriteHotelItemWithDelete(
                hotelWithDetails = hotelWithDetails,
                priceInfo = hotelPrices[hotelWithDetails.hotel.hotelId],
                hotelImage = hotelImages[hotelWithDetails.hotel.hotelId],
                onDelete = {
                    onDeleteClick(hotelWithDetails.hotel.hotelId)
                },
                onClick = {
                    onHotelClick(hotelWithDetails.hotel)
                }
            )
        }
    }
}