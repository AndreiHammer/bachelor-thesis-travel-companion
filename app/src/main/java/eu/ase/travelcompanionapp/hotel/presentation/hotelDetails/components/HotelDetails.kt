package eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.app.navigation.routes.TouristAttractionsRoute
import eu.ase.travelcompanionapp.core.presentation.BlurredAnimatedText
import eu.ase.travelcompanionapp.core.presentation.components.BitmapImageDialog
import eu.ase.travelcompanionapp.core.presentation.components.BitmapPhotoCarousel
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.HotelLocationAction
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.HotelLocationViewModel
import eu.ase.travelcompanionapp.touristattractions.presentation.list.components.TouristAttractionsSection
import eu.ase.travelcompanionapp.touristattractions.presentation.TouristAttractionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetails(
    hotel: Hotel,
    modifier: Modifier = Modifier,
    viewModel: HotelLocationViewModel,
    navController: NavController,
    checkInDate: String,
    checkOutDate: String,
    adults: Int
) {
    val hotelState = viewModel.hotelState.collectAsStateWithLifecycle()
    val touristAttractionsState = viewModel.touristAttractionsState.collectAsStateWithLifecycle()
    val cameraPositionState = rememberCameraPositionState()
    var isImageDialogOpen by remember { mutableStateOf(false) }
    var currentImageIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(hotel.name, hotel.countryCode) {
        viewModel.getHotelDetails(hotel.name, hotel.countryCode)
    }

    val coordinates = hotelState.value.hotel?.let { Pair(it.latitude, it.longitude) }
    LaunchedEffect(coordinates) {
        coordinates?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(it.first, it.second), 15f
            )
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.details)) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.handleAction(HotelLocationAction.OnBackClick, hotel)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.handleAction(
                                HotelLocationAction.OnFavouriteClick(
                                    checkInDate,
                                    checkOutDate,
                                    adults
                                ),
                                hotel
                            )
                        }
                    ) {
                        Icon(
                            imageVector = if (hotelState.value.isFavourite)
                                Icons.Filled.Favorite
                            else
                                Icons.Filled.FavoriteBorder,
                            contentDescription = stringResource(R.string.favourite),
                            tint = Color.Red
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (hotelState.value.isLoading) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    BlurredAnimatedText(text = stringResource(R.string.loading_details))
                }
            }
        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    HotelInfoHeader(
                        hotel = hotel
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    BitmapPhotoCarousel(
                        photos = hotelState.value.photos,
                        onImageClick = { index ->
                            currentImageIndex = index
                            isImageDialogOpen = true
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    AmenitiesSection(amenities = hotel.amenities)
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    MapCard(
                        cameraPositionState = cameraPositionState,
                        hotelState = hotelState.value
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    val attractions = touristAttractionsState.value.attractions
                    
                    TouristAttractionsSection(
                        attractions = attractions,
                        isLoading = touristAttractionsState.value.isLoading,
                        onSeeAllClick = { lat, lng ->
                            viewModel.setupTouristAttractionsNavigation(lat, lng)
                            navController.navigate(
                                TouristAttractionsRoute.TouristAttractionsList
                            )
                        },
                        onAttractionClick = { attraction ->
                            attraction.id?.let {
                                TouristAttractionsViewModel.cacheAttraction(attraction)
                                viewModel.setupTouristAttractionDetailsNavigation(attraction)
                                navController.navigate(
                                    TouristAttractionsRoute.TouristAttractionDetails
                                )
                            }
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    BookingActionCard(
                        onViewOffers = {
                            viewModel.handleAction(
                                HotelLocationAction.OnViewOfferClick(
                                    checkInDate, checkOutDate, adults
                                ),
                                hotel
                            )
                        },
                        bookingDetails = if (checkInDate.isNotEmpty() && checkOutDate.isNotEmpty()) {
                            Triple(checkInDate, checkOutDate, adults)
                        } else null
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            if (isImageDialogOpen && hotelState.value.photos.isNotEmpty()) {
                BitmapImageDialog(
                    photos = hotelState.value.photos,
                    currentImageIndex = currentImageIndex,
                    onDismiss = { isImageDialogOpen = false },
                    onImageChange = { index -> currentImageIndex = index }
                )
            }
        }
    }
}