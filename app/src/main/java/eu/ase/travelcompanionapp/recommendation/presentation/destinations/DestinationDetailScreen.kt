package eu.ase.travelcompanionapp.recommendation.presentation.destinations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.recommendation.domain.model.Destination
import eu.ase.travelcompanionapp.recommendation.presentation.destinations.components.DestinationThumbnail
import eu.ase.travelcompanionapp.recommendation.presentation.destinations.components.HotelSearchFilterDialog
import eu.ase.travelcompanionapp.ui.CompanionTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationDetailScreen(
    navController: NavController,
    destination: Destination,
    viewModel: DestinationViewModel
) {
    val showDialog by viewModel.showHotelSearchDialog.collectAsStateWithLifecycle()
    val selectedCity by viewModel.selectedCityForHotelSearch.collectAsStateWithLifecycle()
    val selectedLocation by viewModel.selectedLocationForHotelSearch.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val currentCheckInDate by viewModel.hotelFilters.selectedCheckInDate.collectAsStateWithLifecycle()
    val currentCheckOutDate by viewModel.hotelFilters.selectedCheckOutDate.collectAsStateWithLifecycle()
    val currentAdults by viewModel.hotelFilters.selectedAdults.collectAsStateWithLifecycle()
    val currentRatings by viewModel.hotelFilters.selectedRatings.collectAsStateWithLifecycle()
    val currentAmenities by viewModel.hotelFilters.selectedAmenities.collectAsStateWithLifecycle()

    val currentDestinationImage = viewModel.getDestinationImage(destination)

    LaunchedEffect(destination.city, destination.country) {
        viewModel.loadDestinationImageForDetail(destination)
    }

    Scaffold(
        topBar = {
            CompanionTopAppBar(
                scrollBehavior = scrollBehavior,
                title = destination.city,
                onNavigationClick = { navController.popBackStack() }
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Button(
                    onClick = { viewModel.showHotelSearchDialog(destination.city, LatLng(destination.latitude, destination.longitude)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.room),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.search_hotels_in_city, destination.city),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {

            item {
                DestinationThumbnail(
                    image = currentDestinationImage,
                    modifier = Modifier.fillMaxWidth(),
                    height = 250
                )
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = destination.city,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${destination.country}, ${destination.continent}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        destination.seasonScore >= 8 -> Color(0xFF4CAF50)
                                        destination.seasonScore >= 6 -> Color(0xFFFF9800)
                                        else -> Color(0xFFF44336)
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${destination.seasonScore}/10 Season Score",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            AssistChip(
                                onClick = { },
                                label = {
                                    Text(
                                        text = destination.budgetLevel,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.about_destination),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = destination.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                        )
                    }
                }
            }

            if (destination.matchReasons.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.why_we_recommend_this),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            destination.matchReasons.forEachIndexed { index, reason ->
                                Row(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = reason,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.weight(1f),
                                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                                    )
                                }
                                if (index < destination.matchReasons.size - 1) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }
            }

            if (destination.bestFor.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.best_for),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(destination.bestFor) { tag ->
                                    FilterChip(
                                        onClick = { },
                                        label = {
                                            Text(
                                                text = tag,
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        },
                                        selected = false
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (destination.popularAttractions.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.popular_attractions),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            destination.popularAttractions.forEach { attraction ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = attraction,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    HotelSearchFilterDialog(
        cityName = selectedCity,
        isVisible = showDialog,
        initialCheckInDate = currentCheckInDate,
        initialCheckOutDate = currentCheckOutDate,
        initialAdults = currentAdults,
        initialRatings = currentRatings,
        initialAmenities = currentAmenities,
        onDismiss = { viewModel.hideHotelSearchDialog() },
        onSearchClick = { checkInDate, checkOutDate, adults, ratings, amenities ->
            viewModel.searchHotelsWithFilters(
                selectedCity,
                selectedLocation,
                checkInDate,
                checkOutDate,
                adults,
                ratings,
                amenities
            )
        }
    )
} 