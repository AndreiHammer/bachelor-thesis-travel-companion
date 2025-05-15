package eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.autocomplete

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.LocationSearchViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.autocomplete.components.SearchInputField
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.autocomplete.components.SuggestionsList
import org.koin.androidx.compose.koinViewModel

@Composable
fun AutoCompleteTextField(
    onCitySelected: (String) -> Unit
) {
    val viewModel: LocationSearchViewModel = koinViewModel()
    val citiesWithCountry by viewModel.citiesWithCountry.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchSuggestions()
    }

    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var filteredSuggestions by remember { mutableStateOf(citiesWithCountry) }
    var showSuggestions by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchInputField(
            textFieldValue = textFieldValue,
            onValueChange = { newValue ->
                if(newValue.text != textFieldValue.text) {
                    textFieldValue = newValue
                    showSuggestions = newValue.text.isNotEmpty()
                    filteredSuggestions = if(newValue.text.isEmpty()) {
                        citiesWithCountry
                    } else {
                        val searchText = newValue.text.trim()
                        val searchTerms = searchText.split(" ")

                        citiesWithCountry.filter { (city, country) ->
                            if (city.contains(searchText, ignoreCase = true) ||
                                country.contains(searchText, ignoreCase = true)) {
                                return@filter true
                            }

                            if (searchTerms.size > 1) {
                                val cityMatch = searchTerms.any { term ->
                                    city.contains(term, ignoreCase = true)
                                }
                                val countryMatch = searchTerms.any { term ->
                                    country.contains(term, ignoreCase = true)
                                }
                                return@filter cityMatch && countryMatch
                            }
                            false
                        }.sortedWith(
                            compareByDescending<Pair<String, String>> { (city, _) ->
                                city.equals(searchText, ignoreCase = true)
                            }.thenByDescending { (city, _) ->
                                city.contains(searchText, ignoreCase = true)
                            }.thenByDescending { (city, country) ->
                                if (searchTerms.size > 1) {
                                    val cityMatch = searchTerms.any { term ->
                                        city.contains(term, ignoreCase = true)
                                    }
                                    val countryMatch = searchTerms.any { term ->
                                        country.contains(term, ignoreCase = true)
                                    }
                                    cityMatch && countryMatch
                                } else {
                                    false
                                }
                            }
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SuggestionsList(
            showSuggestions = showSuggestions,
            suggestions = filteredSuggestions,
            onSuggestionSelected = { city ->
                onCitySelected(city)
                textFieldValue = TextFieldValue(
                    text = city,
                    selection = TextRange(city.length)
                )
                showSuggestions = false
            }
        )
    }
}





