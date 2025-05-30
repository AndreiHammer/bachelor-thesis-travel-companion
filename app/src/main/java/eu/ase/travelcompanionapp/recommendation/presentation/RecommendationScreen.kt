package eu.ase.travelcompanionapp.recommendation.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import eu.ase.travelcompanionapp.recommendation.presentation.components.DestinationRecommendationCard
import eu.ase.travelcompanionapp.recommendation.presentation.components.RecommendationsList
import eu.ase.travelcompanionapp.ui.CompanionTopAppBar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationScreen(
    navController: NavController,
    viewModel: RecommendationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val hotelImages by viewModel.hotelImages.collectAsState()
    val explanations by viewModel.explanations.collectAsState()
    val destinationExplanations by viewModel.destinationExplanations.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Hotels", "Destinations")
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CompanionTopAppBar(
                title = "Recommendations",
                onNavigationClick = { navController.popBackStack() },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = { 
                        if (selectedTabIndex == 0) {
                            viewModel.getRecommendations()
                        } else {
                            viewModel.getDestinationRecommendations()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            
            // Content based on selected tab
            when (selectedTabIndex) {
                0 -> HotelRecommendationsTab(
                    state = state,
                    hotelImages = hotelImages,
                    explanations = explanations,
                    viewModel = viewModel
                )
                1 -> DestinationRecommendationsTab(
                    state = state,
                    destinationExplanations = destinationExplanations,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun HotelRecommendationsTab(
    state: RecommendationState,
    hotelImages: Map<String, coil3.Bitmap?>,
    explanations: Map<String, String>,
    viewModel: RecommendationViewModel
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (state.recommendations.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No Hotel Recommendations Available",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Save some hotels as favorites or make bookings to get personalized recommendations.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            Column {
                if (state.relatedHotels.isEmpty()) {
                    RecommendationsList(
                        hotels = state.recommendations,
                        explanations = explanations,
                        //hotelImages = hotelImages,
                        onHotelClick = { viewModel.navigateToHotelDetails(it) },
                        onSimilarHotelsClick = { viewModel.getSimilarHotels(it) }
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            IconButton(onClick = {
                                viewModel.getRecommendations()
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back to recommendations"
                                )
                            }
                            
                            Text(
                                text = "Similar Hotels",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (state.isLoadingRelated) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        } else {
                            RecommendationsList(
                                hotels = state.relatedHotels,
                                explanations = explanations,
                                //hotelImages = hotelImages,
                                onHotelClick = { viewModel.navigateToHotelDetails(it) },
                                onSimilarHotelsClick = { viewModel.getSimilarHotels(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DestinationRecommendationsTab(
    state: RecommendationState,
    destinationExplanations: Map<String, String>,
    viewModel: RecommendationViewModel
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.isLoadingDestinations) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (state.destinationRecommendations.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No Destination Recommendations Available",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Travel more to get personalized destination recommendations.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Discover Your Next Adventure",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Text(
                        text = "Based on your travel preferences and history",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                items(
                    items = state.destinationRecommendations,
                    key = { it.iataCode }
                ) { destination ->
                    DestinationRecommendationCard(
                        destination = destination,
                        explanation = destinationExplanations[destination.iataCode] ?: "Popular travel destination",
                        onSearchClick = { viewModel.onSearchDestination(it) },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
} 