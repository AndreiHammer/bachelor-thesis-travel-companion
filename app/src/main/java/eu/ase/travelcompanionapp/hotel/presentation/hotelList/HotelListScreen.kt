package eu.ase.travelcompanionapp.hotel.presentation.hotelList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.core.presentation.BlurredAnimatedText
import eu.ase.travelcompanionapp.hotel.domain.Hotel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelListScreen(
    modifier: Modifier = Modifier
) {

    val viewModel: HotelListViewModel = koinViewModel()
    val hotelState by viewModel.hotelState.collectAsState()


    viewModel.getHotelList(city = "BUH", amenities = "", rating = "")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Hotels List") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (hotelState.isLoading) {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    BlurredAnimatedText(text = "Loading hotels...")
                }
            } else {
                HotelList(
                    hotels = hotelState.hotels,
                    onHotelClick = {

                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun HotelList(
    hotels: List<Hotel>,
    onHotelClick: (Hotel) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        hotels.forEach { hotel ->
            item {
                HotelItem(hotel = hotel, onClick = { onHotelClick(hotel) })
            }
        }
    }
}

@Composable
fun HotelItem(hotel: Hotel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = hotel.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier
                .padding(8.dp))
            Text(text = hotel.countryCode, style = MaterialTheme.typography.bodyMedium)
        }
    }
}