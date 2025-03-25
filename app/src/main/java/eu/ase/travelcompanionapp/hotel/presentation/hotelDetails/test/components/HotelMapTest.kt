package eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.test.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.HotelLocationViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.components.GoogleMapComponent

@Composable
fun HotelMapTest(
    modifier: Modifier = Modifier,
    hotelState: HotelLocationViewModel.HotelState,
    onNavigateToOffers: () -> Unit
) {
    val cameraPositionState = rememberCameraPositionState()

    val coordinates = hotelState.hotel?.let { Pair(it.latitude, it.longitude) }
    LaunchedEffect(coordinates) {
        coordinates?.let {
            cameraPositionState.position =
                LatLng(it.first, it.second).let { it2 ->
                    CameraPosition.fromLatLngZoom(
                        it2,
                        15f
                    )
                }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Add a test mode indicator card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TEST MODE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Text(
                    text = "Photos are not loaded in test mode to save API costs",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                
                // Show location information
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Location: ${coordinates?.first}, ${coordinates?.second}",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                
                Text(
                    text = "Country: ${hotelState.hotel?.countryCode ?: "Unknown"}",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNavigateToOffers,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(stringResource(R.string.view_offers), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        GoogleMapComponent(
            cameraPositionState = cameraPositionState,
            hotelState = hotelState,
            modifier = Modifier.weight(1f)
        )
    }
}