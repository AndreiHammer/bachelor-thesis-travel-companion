package eu.ase.travelcompanionapp.hotel.presentation.hotelList.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.hotel.domain.Hotel


@Composable
fun HotelList(
    hotels: List<Hotel>,
    onHotelClick: (Hotel) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(hotels){ hotel ->
            HotelItem(hotel = hotel, onClick = { onHotelClick(hotel) })
        }
    }
}