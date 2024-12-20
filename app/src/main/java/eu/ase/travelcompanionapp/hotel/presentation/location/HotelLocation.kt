package eu.ase.travelcompanionapp.hotel.presentation.location

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import eu.ase.travelcompanionapp.core.presentation.BlurredAnimatedText
import eu.ase.travelcompanionapp.hotel.presentation.location.components.GoogleMapComponent
import eu.ase.travelcompanionapp.hotel.presentation.location.components.ImageDialog
import eu.ase.travelcompanionapp.hotel.presentation.location.components.ImageList
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelLocationScreen(
    country: String,
    hotelName: String,
    modifier: Modifier = Modifier
) {
    val viewModel: HotelLocationViewModel = koinViewModel()

    val hotelState = viewModel.hotelState.collectAsState()

    LaunchedEffect(hotelName, country) {
        viewModel.getHotelDetails(hotelName, country)
    }

    val isLoading = hotelState.value.coordinates == null || hotelState.value.photos.isEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = hotelName) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    BlurredAnimatedText(text = "Loading details...")
                }
            } else {
                HotelMap(
                    modifier = Modifier.fillMaxSize(),
                    hotelState = hotelState.value
                )
            }
        }
    }
}

@Composable
fun HotelMap(
    modifier: Modifier = Modifier,
    hotelState: HotelLocationViewModel.HotelState
) {
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(hotelState.coordinates) {
        hotelState.coordinates?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(it.first, it.second),
                15f
            )
        }
    }

    var isDialogOpen by remember { mutableStateOf(false) }
    var currentImageIndex by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ImageList(
            hotelState = hotelState,
            onImageClick = { index ->
                currentImageIndex = index
                isDialogOpen = true
            },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.height(8.dp))


        GoogleMapComponent(
            cameraPositionState = cameraPositionState,
            hotelState = hotelState,
            modifier = Modifier.weight(1f)
        )
    }

    if (isDialogOpen) {
        ImageDialog(
            hotelState = hotelState,
            currentImageIndex = currentImageIndex,
            onDismiss = { isDialogOpen = false },
            onImageChange = { index -> currentImageIndex = index }
        )
    }
}