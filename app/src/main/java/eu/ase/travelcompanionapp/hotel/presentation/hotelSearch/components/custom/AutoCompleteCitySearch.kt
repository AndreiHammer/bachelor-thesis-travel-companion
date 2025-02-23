package eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.custom

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.LocationSearchViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AutoCompleteTextField(
    onCitySelected: (String) -> Unit
) {

    val viewModel: LocationSearchViewModel = koinViewModel()
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchSuggestions()
    }


    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var filteredSuggestions by remember { mutableStateOf(suggestions) }
    var showSuggestions by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            BasicTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    if (newValue.text != textFieldValue.text) {
                        textFieldValue = newValue
                        showSuggestions = newValue.text.isNotEmpty()
                        filteredSuggestions = if (newValue.text.isEmpty()) {
                            suggestions
                        } else {
                            suggestions.filter { it.contains(newValue.text, ignoreCase = true) }
                        }

                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
                    .focusRequester(focusRequester),
                textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
                cursorBrush = SolidColor(Color.Blue),
                decorationBox = { innerTextField ->
                    if (textFieldValue.text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.enter_city),
                            style = TextStyle(color = Color.Gray, fontSize = 18.sp)
                        )
                    }
                    innerTextField()
                }
            )
        }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(8.dp))
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .animateContentSize()
        ) {
            if (showSuggestions && filteredSuggestions.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    items(
                        items = filteredSuggestions,
                        key = { suggestion -> suggestion }
                    ) { suggestion ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onCitySelected(suggestion)
                                    textFieldValue = TextFieldValue(
                                        text = suggestion,
                                        selection = TextRange(suggestion.length)
                                    )
                                    showSuggestions = false
                                }
                                .padding(12.dp)
                        ) {
                            Text(
                                text = suggestion,
                                style = TextStyle(fontSize = 18.sp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            } else if (showSuggestions) {
                Text(
                    text = stringResource(R.string.no_suggestions_available),
                    style = TextStyle(color = Color.Gray, fontSize = 16.sp),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}