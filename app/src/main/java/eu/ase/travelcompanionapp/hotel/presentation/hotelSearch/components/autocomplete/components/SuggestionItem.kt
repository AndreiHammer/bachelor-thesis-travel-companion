package eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.autocomplete.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.ase.travelcompanionapp.R

@Composable
fun SuggestionItem(
    cityCountryPair: Pair<String, String>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (city, country) = cityCountryPair

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = city,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = country,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Gray
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun NoSuggestionsMessage(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.no_suggestions_available),
        style = TextStyle(color = Color.Gray, fontSize = 16.sp),
        modifier = modifier.padding(8.dp)
    )
}