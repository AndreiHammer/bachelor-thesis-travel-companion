package eu.ase.travelcompanionapp.touristattractions.presentation.details.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.core.presentation.components.UrlImageDialog
import eu.ase.travelcompanionapp.core.presentation.components.UrlPhotoCarousel
import eu.ase.travelcompanionapp.touristattractions.domain.model.Price

@Composable
fun AttractionPhotoGallery(
    photos: List<String>,
    modifier: Modifier = Modifier
) {
    var isImageDialogOpen by remember { mutableStateOf(false) }
    var currentImageIndex by remember { mutableIntStateOf(0) }

    UrlPhotoCarousel(
        photoUrls = photos,
        onImageClick = { index ->
            currentImageIndex = index
            isImageDialogOpen = true
        },
        modifier = modifier
    )

    if (isImageDialogOpen && photos.isNotEmpty()) {
        UrlImageDialog(
            photoUrls = photos,
            currentImageIndex = currentImageIndex,
            onDismiss = { isImageDialogOpen = false },
            onImageChange = { index -> currentImageIndex = index }
        )
    }
}

@Composable
fun AttractionTitleSection(
    name: String?,
    type: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = name ?: stringResource(R.string.unknown_attraction),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        type?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AttractionDetailRating(
    rating: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFFFC107)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Rating: $rating",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun AttractionDescriptionCard(
    description: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.description),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description ?: stringResource(R.string.no_description_available),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun AttractionPriceCard(
    price: Price,
    modifier: Modifier = Modifier
) {
    if (price.amount != null && price.currencyCode != null) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.price),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "${price.amount} ${price.currencyCode}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun AttractionLocationCard(
    latitude: Double,
    longitude: Double,
    attractionName: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.location),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            AttractionMap(
                latitude = latitude,
                longitude = longitude,
                attractionName = attractionName
            )
        }
    }
}

@Composable
fun AttractionBookingButton(
    bookingLink: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Button(
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(bookingLink))
            context.startActivity(intent)
        },
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.book_this_attraction))
    }
} 