package eu.ase.travelcompanionapp.touristattractions.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.touristattractions.domain.model.TouristAttraction
import eu.ase.travelcompanionapp.touristattractions.presentation.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TouristAttractionsListScreen(
    viewModel: TouristAttractionsViewModel,
    latitude: Double,
    longitude: Double
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(latitude, longitude) {
        viewModel.loadAttractionsByLocation(latitude, longitude)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tourist_attractions_nearby)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        AttractionListContent(
            state = state,
            onAttractionClick = { viewModel.navigateToAttractionDetails(it) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
private fun AttractionListContent(
    state: TouristAttractionsViewModel.TouristAttractionsState,
    onAttractionClick: (TouristAttraction) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when {
            state.isLoading -> {
                AttractionLoadingState(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            state.error != null -> {
                AttractionErrorState(
                    errorMessage = state.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            state.attractions.isEmpty() -> {
                AttractionEmptyState(
                    message = stringResource(R.string.no_tourist_attractions_found_nearby),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                AttractionGrid(
                    attractions = state.attractions,
                    onAttractionClick = onAttractionClick
                )
            }
        }
    }
}

@Composable
private fun AttractionGrid(
    attractions: List<TouristAttraction>,
    onAttractionClick: (TouristAttraction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(attractions) { attraction ->
            EnhancedAttractionCard(
                attraction = attraction,
                onClick = { onAttractionClick(attraction) }
            )
        }
    }
} 