package eu.ase.travelcompanionapp.recommendation.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.components.HotelItem

@Composable
fun RecommendationsList(
    hotels: List<Hotel>,
    explanations: Map<String, String>,
    //hotelImages: Map<String, Bitmap?> = emptyMap(),
    onHotelClick: (hotel: Hotel) -> Unit,
    onSimilarHotelsClick: (hotel: Hotel) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(
            items = hotels,
            key = { it.hotelId }
        ) { hotel ->
            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {

                HotelItem(
                    hotel = hotel,
                    //hotelImage = hotelImages[hotel.hotelId],
                    onClick = { onHotelClick(hotel) },
                    modifier = Modifier.fillMaxWidth()
                )

                RecommendationExtraInfo(
                    explanation = explanations[hotel.hotelId],
                    onSimilarClick = { onSimilarHotelsClick(hotel) }
                )
            }
        }
    }
}

@Composable
private fun RecommendationExtraInfo(
    explanation: String?,
    onSimilarClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Why We Recommend This",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
            
            Text(
                text = explanation ?: "Recommended based on your preferences",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            TextButton(
                onClick = onSimilarClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("View Similar Hotels")
            }
        }
    }
} 