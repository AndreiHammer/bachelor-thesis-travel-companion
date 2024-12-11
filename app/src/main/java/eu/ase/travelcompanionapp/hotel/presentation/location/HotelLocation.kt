package eu.ase.travelcompanionapp.hotel.presentation.location

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelLocationScreen(
    country: String,
    latitude: Double,
    longitude: Double,
    hotelName: String,
    modifier: Modifier = Modifier
) {
    val viewModel: HotelLocationViewModel = koinViewModel()

    val hotelState = viewModel.hotelState.collectAsState()

    LaunchedEffect(hotelName, country) {
        viewModel.getHotelDetails(hotelName, country)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = hotelName) }
            )
        }
    ) { paddingValues ->
        HotelMap(
            modifier = modifier.fillMaxSize().padding(paddingValues),
            country = country,
            latitude = latitude,
            longitude = longitude,
            hotelName = hotelName,
            hotelState = hotelState.value
        )
    }
}

@Composable
fun HotelMap(
    modifier: Modifier = Modifier,
    country: String,
    latitude: Double,
    longitude: Double,
    hotelName: String,
    hotelState: HotelLocationViewModel.HotelState
) {
    val location = LatLng(latitude, longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 15f)
    }

    Column(modifier = modifier.fillMaxSize()) {

        Column(modifier = Modifier.weight(1f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = MarkerState(position = location),
                    title = hotelName,
                    snippet = "Hotel in $country"
                )
            }
        }

        Column(modifier = Modifier.weight(1f).padding(8.dp)) {
            if (hotelState.photos.isNotEmpty()) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(hotelState.photos) { photoUri ->
                        Image(
                            bitmap = photoUri.asImageBitmap(),
                            contentDescription = "$hotelName Photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(8.dp)
                        )
                    }
                }
            } else if (hotelState.errorMessage != null) {
                Text(
                    text = hotelState.errorMessage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Loading photos...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
