package eu.ase.travelcompanionapp.touristattractions.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.touristattractions.domain.model.TouristAttraction
import eu.ase.travelcompanionapp.touristattractions.presentation.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TouristAttractionDetailsScreen(
    viewModel: TouristAttractionsViewModel,
    attractionId: String
) {
    val attraction = remember(attractionId) { viewModel.getAttractionById(attractionId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(attraction?.name ?: stringResource(R.string.details)) },
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
        if (attraction == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.attraction_not_found))
            }
        } else {
            AttractionDetailContent(
                attraction = attraction,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }
}

@Composable
private fun AttractionDetailContent(
    attraction: TouristAttraction,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        if (attraction.pictures.isNotEmpty()) {
            AttractionPhotoGallery(
                photos = attraction.pictures,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AttractionTitleSection(
                name = attraction.name,
                type = attraction.type
            )

            Spacer(modifier = Modifier.height(16.dp))

            attraction.rating?.let { rating ->
                AttractionDetailRating(rating = rating)
                Spacer(modifier = Modifier.height(16.dp))
            }

            AttractionDescriptionCard(description = attraction.description)
            Spacer(modifier = Modifier.height(16.dp))

            attraction.price?.let { price ->
                AttractionPriceCard(price = price)
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (attraction.latitude != null && attraction.longitude != null) {
                AttractionLocationCard(
                    latitude = attraction.latitude!!,
                    longitude = attraction.longitude!!,
                    attractionName = attraction.name
                )
            }

            attraction.bookingLink?.let { bookingLink ->
                Spacer(modifier = Modifier.height(32.dp))
                AttractionBookingButton(bookingLink = bookingLink)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

