package eu.ase.travelcompanionapp.hotel.presentation.hotelSearch

import android.Manifest
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.autocomplete.AutoCompleteTextField
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.custom.LocationSearchFab
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.custom.NearbyAccomodationsCard
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.custom.RecommendationCard
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.filters.CityFilterSearch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun LocationSearchScreen(
    modifier: Modifier = Modifier,
    onAction: (LocationSearchAction) -> Unit,
    sharedViewModel: SharedViewModel
) {
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val context = LocalContext.current
    val viewModel = koinViewModel<LocationSearchViewModel>()
    val scrollState = rememberScrollState()

    val isWaitingForPermission = remember { mutableStateOf(false) }
    var city by remember { mutableStateOf("") }
    var showSearchContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showSearchContent = true
    }

    LaunchedEffect(locationPermissionState.status) {
        if (isWaitingForPermission.value && locationPermissionState.status is PermissionStatus.Granted) {
            isWaitingForPermission.value = false
            viewModel.performNearbySearch(
                context = context,
                sharedViewModel = sharedViewModel,
                radius = 20,
                onResult = { success ->
                    if (success) {
                        onAction(LocationSearchAction.OnNearbySearchClick)
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.unable_to_get_your_location_please_check_your_location_settings),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
    }

    onAction(LocationSearchAction.OnRatingSelected(emptySet()))
    onAction(LocationSearchAction.OnAmenitiesSelected(emptySet()))
    onAction(LocationSearchAction.OnOfferDetailsSet("", "", 0))

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name_2)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = stringResource(R.string.user_profile),
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                onAction(LocationSearchAction.OnProfileClick)
                            },
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            )
        },
        floatingActionButton = {
            LocationSearchFab(
                onClick = {
                    onAction(LocationSearchAction.OnMapClick)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = showSearchContent,
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { 40 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier
                                    .size(28.dp)
                                    .padding(end = 8.dp)
                            )
                            
                            Text(
                                text = stringResource(R.string.plan_your_stay),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = showSearchContent,
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { 40 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.looking_for_quick_options),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    NearbyAccomodationsCard(
                        onNearbySearchClick = {
                            when (locationPermissionState.status) {
                                is PermissionStatus.Granted -> {
                                    viewModel.performNearbySearch(
                                        context = context,
                                        sharedViewModel = sharedViewModel,
                                        radius = 20,
                                        onResult = { success ->
                                            if (success) {
                                                onAction(LocationSearchAction.OnNearbySearchClick)
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.unable_to_get_your_location_please_check_your_location_settings),
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    )
                                }
                                is PermissionStatus.Denied -> {
                                    isWaitingForPermission.value = true
                                    locationPermissionState.launchPermissionRequest()
                                }
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    RecommendationCard(
                        onRecommendationClick = {
                            onAction(LocationSearchAction.OnRecommendationsClick)
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = showSearchContent,
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { 40 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.where_to),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        AutoCompleteTextField(
                            onCitySelected = { selectedCity ->
                                city = selectedCity
                                onAction(LocationSearchAction.OnCitySelected(selectedCity))
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = showSearchContent,
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { 40 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                CityFilterSearch(
                    city = city,
                    onAction = onAction
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
