package eu.ase.travelcompanionapp.hotel.presentation.hotelOffers.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.hotel.domain.model.HotelOffer
import androidx.compose.foundation.lazy.items


@Composable
fun HotelOffersSection(
    hotelOffers: List<HotelOffer>,
    errorMessage: String,
    onBookNow: (String) -> Unit
) {
    if (hotelOffers.isEmpty()) {
        Text(errorMessage, modifier = Modifier.padding(8.dp))
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(hotelOffers) { hotelOffer ->
                HotelOfferItem(hotelOffer = hotelOffer, onBookNow = onBookNow)
            }
        }
    }
}

