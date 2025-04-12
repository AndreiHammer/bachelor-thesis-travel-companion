package eu.ase.travelcompanionapp.hotel.presentation.hotelOffers.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.hotel.domain.model.HotelOffer
import eu.ase.travelcompanionapp.user.domain.model.Currency

@Composable
fun HotelOfferItem(
    hotelOffer: HotelOffer,
    onBookNow: (HotelOffer) -> Unit,
    convertedPrices: Map<String, Currency>
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        hotelOffer.hotel?.name?.let {
            Text(
                text = it,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (hotelOffer.offers.isNotEmpty()) {
            hotelOffer.offers.forEach { offer ->
                offer.id?.let {
                    OfferCard(
                        offer = offer,
                        onBookNow = onBookNow,
                        convertedPrice = convertedPrices[offer.id],
                        hotelOffer = hotelOffer
                    )
                }
            }
        } else {
            Text(
                text = stringResource(R.string.no_offers_available_for_this_hotel),
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}