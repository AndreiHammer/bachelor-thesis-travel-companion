package eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.autocomplete.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SuggestionsList(
    showSuggestions: Boolean,
    suggestions: List<Pair<String, String>>,
    onSuggestionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(8.dp))
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .animateContentSize()
    ) {
        if (showSuggestions && suggestions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp),
                horizontalAlignment = Alignment.Start
            ) {
                items(
                    items = suggestions,
                    key = { (city, _) -> city }
                ) { suggestion ->
                    SuggestionItem(
                        cityCountryPair = suggestion,
                        onClick = { onSuggestionSelected(suggestion.first) }
                    )
                }
            }
        } else if (showSuggestions) {
            NoSuggestionsMessage()
        }
    }
}