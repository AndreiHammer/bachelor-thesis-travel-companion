package eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.R

@Composable
fun PreferredActivitiesQuestion(
    selectedActivities: List<String>,
    onActivitiesChanged: (List<String>) -> Unit
) {
    val activityOptions = listOf(
        "Cultural exploration (museums, historical sites)",
        "Nature and wildlife",
        "Beach and water sports",
        "Hiking and adventure",
        "Nightlife and entertainment",
        "Food and culinary experiences",
        "Shopping",
        "Wellness and relaxation (spa, yoga, retreats)",
        "Festivals and local events",
        "Winter sports (skiing, snowboarding)"
    )

    QuestionCard(
        title = stringResource(R.string.preferred_activities),
        subtitle = stringResource(R.string.select_all_that_apply)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            activityOptions.chunked(1).forEach { rowActivities ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowActivities.forEach { activity ->
                        CheckboxCard(
                            text = activity,
                            checked = selectedActivities.contains(activity),
                            onCheckedChange = { isChecked ->
                                val newActivities = if (isChecked) {
                                    selectedActivities + activity
                                } else {
                                    selectedActivities - activity
                                }
                                onActivitiesChanged(newActivities)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClimatePreferenceQuestion(
    selectedClimate: String,
    onClimateSelected: (String) -> Unit
) {
    val climateOptions = listOf(
        "Warm and sunny",
        "Mild and temperate",
        "Cold and snowy",
        "No preference"
    )

    QuestionCard(
        title = stringResource(R.string.climate_preference),
        subtitle = stringResource(R.string.choose_one)
    ) {
        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            climateOptions.forEach { climate ->
                OptionCard(
                    title = climate,
                    selected = climate == selectedClimate,
                    onClick = { onClimateSelected(climate) }
                )
            }
        }
    }
}

@Composable
fun TravelStyleQuestion(
    selectedStyle: String,
    onStyleSelected: (String) -> Unit
) {
    val styleOptions = listOf(
        "Adventure seeker",
        "Culture and history enthusiast", 
        "Luxury and comfort",
        "Budget traveler",
        "Nature lover",
        "Off-the-beaten-path explorer"
    )

    QuestionCard(
        title = stringResource(R.string.travel_style),
        subtitle = stringResource(R.string.pick_the_one_that_fits_you_best)
    ) {
        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            styleOptions.forEach { style ->
                OptionCard(
                    title = style,
                    selected = style == selectedStyle,
                    onClick = { onStyleSelected(style) }
                )
            }
        }
    }
}

@Composable
fun TripDurationQuestion(
    selectedDuration: String,
    onDurationSelected: (String) -> Unit
) {
    val durationOptions = listOf(
        "Weekend getaway (1–3 days)",
        "Short trip (4–7 days)",
        "Medium (1–2 weeks)",
        "Extended travel (2+ weeks)"
    )

    QuestionCard(
        title = stringResource(R.string.trip_duration),
        subtitle = stringResource(R.string.typical_trip_length)
    ) {
        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            durationOptions.forEach { duration ->
                OptionCard(
                    title = duration,
                    selected = duration == selectedDuration,
                    onClick = { onDurationSelected(duration) }
                )
            }
        }
    }
}

@Composable
fun CompanionsQuestion(
    selectedCompanions: String,
    onCompanionsSelected: (String) -> Unit
) {
    val companionOptions = listOf(
        "Solo",
        "Partner",
        "Friends",
        "Family with kids",
        "Group (5+)"
    )

    QuestionCard(
        title = stringResource(R.string.companions),
        subtitle = stringResource(R.string.choose_the_usual_scenario)
    ) {
        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            companionOptions.forEach { companion ->
                OptionCard(
                    title = companion,
                    selected = companion == selectedCompanions,
                    onClick = { onCompanionsSelected(companion) }
                )
            }
        }
    }
}

@Composable
fun CulturalOpennessQuestion(
    culturalOpenness: Int,
    onOpennessChanged: (Int) -> Unit
) {
    QuestionCard(
        title = stringResource(R.string.cultural_openness),
        subtitle = stringResource(R.string.cultural_openness_scale)
    ) {
        ImportanceSlider(
            label = "Cultural Openness",
            value = culturalOpenness,
            onValueChange = onOpennessChanged
        )
    }
}

@Composable
fun PreferredCountryQuestion(
    preferredCountry: String,
    onCountryChanged: (String) -> Unit
) {
    QuestionCard(
        title = stringResource(R.string.preferred_country),
        subtitle = stringResource(R.string.preferred_country_optional)
    ) {
        OutlinedTextField(
            value = preferredCountry,
            onValueChange = onCountryChanged,
            placeholder = { Text("e.g., \"Japan\", \"Italy\", \"Somewhere in Scandinavia\"") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun BucketListThemesQuestion(
    selectedThemes: List<String>,
    onThemesChanged: (List<String>) -> Unit
) {
    val themeOptions = listOf(
        "Tropical islands",
        "Historical cities",
        "Remote villages",
        "Major world cities",
        "Safari and wildlife parks",
        "National parks",
        "Castles and ancient ruins",
        "Coastal towns",
        "Ski resorts",
        "Desert landscapes"
    )

    QuestionCard(
        title = stringResource(R.string.bucket_list_themes),
        subtitle = stringResource(R.string.select_all_that_inspire_you)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            themeOptions.chunked(2).forEach { rowThemes ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowThemes.forEach { theme ->
                        CheckboxCard(
                            text = theme,
                            checked = selectedThemes.contains(theme),
                            onCheckedChange = { isChecked ->
                                val newThemes = if (isChecked) {
                                    selectedThemes + theme
                                } else {
                                    selectedThemes - theme
                                }
                                onThemesChanged(newThemes)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowThemes.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetRangeQuestion(
    selectedBudget: String,
    onBudgetSelected: (String) -> Unit
) {
    val budgetOptions = listOf(
        "Budget ($)",
        "Mid-range ($$)",
        "Premium ($$$)",
        "Luxury ($$$$)",
        "No preference"
    )

    QuestionCard(
        title = stringResource(R.string.budget_range),
        subtitle = stringResource(R.string.per_night)
    ) {
        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            budgetOptions.forEach { budget ->
                OptionCard(
                    title = budget,
                    selected = budget == selectedBudget,
                    onClick = { onBudgetSelected(budget) }
                )
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
        "Western Europe",
        "Eastern Europe", 
        "Southeast Asia",
        "East Asia",
        "North America",
        "South America",
        "Africa",
        "Middle East",
        "Oceania",
        "Central Asia"
    )

    QuestionCard(
        title = stringResource(R.string.preferred_continents),
        subtitle = stringResource(R.string.where_do_you_like_to_travel)
    ) {
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