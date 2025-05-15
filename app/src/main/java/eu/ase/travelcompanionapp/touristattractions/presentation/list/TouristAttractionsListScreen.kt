package eu.ase.travelcompanionapp.touristattractions.presentation.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.touristattractions.domain.model.TouristAttraction
import eu.ase.travelcompanionapp.touristattractions.presentation.TouristAttractionsViewModel
import eu.ase.travelcompanionapp.touristattractions.presentation.TouristSharedViewModel
import eu.ase.travelcompanionapp.touristattractions.presentation.list.components.AttractionEmptyState
import eu.ase.travelcompanionapp.touristattractions.presentation.list.components.AttractionErrorState
import eu.ase.travelcompanionapp.touristattractions.presentation.list.components.AttractionLoadingState
import eu.ase.travelcompanionapp.touristattractions.presentation.list.components.EnhancedAttractionCard
import eu.ase.travelcompanionapp.ui.CompanionTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TouristAttractionsListScreen(
    viewModel: TouristAttractionsViewModel,
    sharedViewModel: TouristSharedViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val latitude by sharedViewModel.selectedLatitude.collectAsStateWithLifecycle()
    val longitude by sharedViewModel.selectedLongitude.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(latitude, longitude) {
        if (latitude != 0.0 && longitude != 0.0) {
            viewModel.loadAttractionsByLocation(latitude, longitude)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CompanionTopAppBar(
                title = stringResource(R.string.tourist_attractions_nearby),
                onNavigationClick = { viewModel.navigateBack() },
                scrollBehavior = scrollBehavior
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
        columns = GridCells.Adaptive(minSize = 180.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(attractions) { attraction ->
            EnhancedAttractionCard(
                attraction = attraction,
                onClick = { onAttractionClick(attraction) },
                modifier = Modifier
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
} 