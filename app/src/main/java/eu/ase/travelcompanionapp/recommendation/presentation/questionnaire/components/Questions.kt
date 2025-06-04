package eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.R

@Composable
fun BudgetRangeQuestion(
    selectedBudget: String,
    onBudgetSelected: (String) -> Unit
) {
    val budgetOptions = listOf(
        "Budget-friendly" to "$0 - $100",
        "Mid-range" to "$100 - $300",
        "Luxury" to "$300+",
        "Any budget" to "No preference"
    )

    QuestionCard(title = stringResource(R.string.budget_range), subtitle = stringResource(R.string.per_night)) {
        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            budgetOptions.forEach { (budget, description) ->
                OptionCard(
                    title = budget,
                    subtitle = description,
                    selected = budget == selectedBudget,
                    onClick = { onBudgetSelected(budget) }
                )
            }
        }
    }
}

@Composable
fun TravelPurposeQuestion(
    selectedPurpose: String,
    onPurposeSelected: (String) -> Unit
) {
    val purposeOptions = listOf(
        "Business", "Leisure", "Family vacation",
        "Romantic getaway", "Adventure travel", "Relaxation"
    )

    QuestionCard(title = stringResource(R.string.travel_purpose), subtitle = stringResource(R.string.main_reason_for_traveling)) {
        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            purposeOptions.forEach { purpose ->
                OptionCard(
                    title = purpose,
                    selected = purpose == selectedPurpose,
                    onClick = { onPurposeSelected(purpose) }
                )
            }
        }
    }
}

@Composable
fun GroupSizeQuestion(
    selectedSize: String,
    onSizeSelected: (String) -> Unit
) {
    val sizeOptions = listOf(
        "Solo traveler", "Couple", "Small group (3-4)", "Large group (5+)"
    )

    QuestionCard(title = stringResource(R.string.group_size), subtitle = stringResource(R.string.how_many_travelers)) {
        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sizeOptions.forEach { size ->
                OptionCard(
                    title = size,
                    selected = size == selectedSize,
                    onClick = { onSizeSelected(size) }
                )
            }
        }
    }
}

@Composable
fun AccommodationTypeQuestion(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    val typeOptions = listOf(
        "Hotel", "Resort", "Boutique Hotel", "Business Hotel", "Any type"
    )

    QuestionCard(title = stringResource(R.string.accommodation_type), subtitle = stringResource(R.string.preferred_style)) {
        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            typeOptions.forEach { type ->
                OptionCard(
                    title = type,
                    selected = type == selectedType,
                    onClick = { onTypeSelected(type) }
                )
            }
        }
    }
}

@Composable
fun LocationPreferenceQuestion(
    selectedLocation: String,
    onLocationSelected: (String) -> Unit
) {
    val locationOptions = listOf(
        "City center", "Near airport", "Beach area",
        "Mountain area", "Countryside", "Any location"
    )

    QuestionCard(title = stringResource(R.string.location_preference), subtitle = stringResource(R.string.where_to_stay)) {
        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            locationOptions.forEach { location ->
                OptionCard(
                    title = location,
                    selected = location == selectedLocation,
                    onClick = { onLocationSelected(location) }
                )
            }
        }
    }
}

@Composable
fun ImportanceRatingsSection(
    priceImportance: Int,
    ratingImportance: Int,
    amenitiesImportance: Int,
    locationImportance: Int,
    onPriceImportanceChanged: (Int) -> Unit,
    onRatingImportanceChanged: (Int) -> Unit,
    onAmenitiesImportanceChanged: (Int) -> Unit,
    onLocationImportanceChanged: (Int) -> Unit
) {
    QuestionCard(title = stringResource(R.string.importance_factors), subtitle = stringResource(R.string.rate_from_1_10)) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ImportanceSlider(
                label = stringResource(R.string.price),
                value = priceImportance,
                onValueChange = onPriceImportanceChanged
            )
            ImportanceSlider(
                label = stringResource(R.string.hotel_rating),
                value = ratingImportance,
                onValueChange = onRatingImportanceChanged
            )
            ImportanceSlider(
                label = stringResource(R.string.amenities),
                value = amenitiesImportance,
                onValueChange = onAmenitiesImportanceChanged
            )
            ImportanceSlider(
                label = stringResource(R.string.location),
                value = locationImportance,
                onValueChange = onLocationImportanceChanged
            )
        }
    }
}

@Composable
fun PreferredAmenitiesQuestion(
    selectedAmenities: List<String>,
    onAmenitiesChanged: (List<String>) -> Unit
) {
    val amenityOptions = listOf(
        "WiFi", "Parking", "Swimming Pool", "Gym", "Spa", "Restaurant",
        "Room Service", "Business Center", "Pet Friendly", "Airport Shuttle",
        "Concierge", "Laundry Service"
    )

    QuestionCard(title = stringResource(R.string.important_amenities), subtitle = stringResource(R.string.select_all_that_matter_to_you)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            amenityOptions.chunked(2).forEach { rowAmenities ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowAmenities.forEach { amenity ->
                        CheckboxCard(
                            text = amenity,
                            checked = selectedAmenities.contains(amenity),
                            onCheckedChange = { isChecked ->
                                val newAmenities = if (isChecked) {
                                    selectedAmenities + amenity
                                } else {
                                    selectedAmenities - amenity
                                }
                                onAmenitiesChanged(newAmenities)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowAmenities.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun PreferredContinentsQuestion(
    selectedContinents: List<String>,
    onContinentsChanged: (List<String>) -> Unit
) {
    val continentOptions = listOf(
        "Europe", "Asia", "North America", "South America", "Africa", "Oceania"
    )

    QuestionCard(title = stringResource(R.string.preferred_continents), subtitle = stringResource(R.string.where_do_you_like_to_travel)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            continentOptions.chunked(2).forEach { rowContinents ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowContinents.forEach { continent ->
                        CheckboxCard(
                            text = continent,
                            checked = selectedContinents.contains(continent),
                            onCheckedChange = { isChecked ->
                                val newContinents = if (isChecked) {
                                    selectedContinents + continent
                                } else {
                                    selectedContinents - continent
                                }
                                onContinentsChanged(newContinents)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowContinents.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}