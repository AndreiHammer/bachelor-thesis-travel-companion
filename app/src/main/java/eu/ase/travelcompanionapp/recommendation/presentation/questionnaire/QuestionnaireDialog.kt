package eu.ase.travelcompanionapp.recommendation.presentation.questionnaire

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.components.BudgetRangeQuestion
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.components.BucketListThemesQuestion
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.components.ClimatePreferenceQuestion
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.components.CompanionsQuestion
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.components.CulturalOpennessQuestion
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.components.PreferredActivitiesQuestion
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.components.PreferredContinentsQuestion
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.components.PreferredCountryQuestion
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.components.TravelStyleQuestion
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.components.TripDurationQuestion

@Composable
fun QuestionnaireDialog(
    viewModel: QuestionnaireViewModel,
    isEditMode: Boolean = false,
    onDismiss: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) {
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isEditMode) stringResource(R.string.edit_travel_preferences) else stringResource(
                                    R.string.travel_preferences
                                ),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = if (isEditMode) stringResource(R.string.update_your_travel_style) else stringResource(
                                    R.string.personalize_your_journey
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        
                        IconButton(
                            onClick = onDismiss,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)
                            )
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        Text(
                            text = if (isEditMode) stringResource(R.string.modify_your_answers_to_update_your_destination_recommendations)
                            else stringResource(R.string.answer_a_few_questions_to_get_personalized_destination_recommendations),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        PreferredActivitiesQuestion(
                            selectedActivities = state.preferences.preferredActivities,
                            onActivitiesChanged = { viewModel.updatePreferredActivities(it) }
                        )
                    }

                    item {
                        ClimatePreferenceQuestion(
                            selectedClimate = state.preferences.climatePreference,
                            onClimateSelected = { viewModel.updateClimatePreference(it) }
                        )
                    }

                    item {
                        TravelStyleQuestion(
                            selectedStyle = state.preferences.travelStyle,
                            onStyleSelected = { viewModel.updateTravelStyle(it) }
                        )
                    }

                    item {
                        TripDurationQuestion(
                            selectedDuration = state.preferences.tripDuration,
                            onDurationSelected = { viewModel.updateTripDuration(it) }
                        )
                    }

                    item {
                        CompanionsQuestion(
                            selectedCompanions = state.preferences.companions,
                            onCompanionsSelected = { viewModel.updateCompanions(it) }
                        )
                    }

                    item {
                        CulturalOpennessQuestion(
                            culturalOpenness = state.preferences.culturalOpenness,
                            onOpennessChanged = { viewModel.updateCulturalOpenness(it) }
                        )
                    }

                    item {
                        PreferredCountryQuestion(
                            preferredCountry = state.preferences.preferredCountry,
                            onCountryChanged = { viewModel.updatePreferredCountry(it) }
                        )
                    }

                    item {
                        BucketListThemesQuestion(
                            selectedThemes = state.preferences.bucketListThemes,
                            onThemesChanged = { viewModel.updateBucketListThemes(it) }
                        )
                    }

                    item {
                        BudgetRangeQuestion(
                            selectedBudget = state.preferences.budgetRange,
                            onBudgetSelected = { viewModel.updateBudgetRange(it) }
                        )
                    }

                    item {
                        PreferredContinentsQuestion(
                            selectedContinents = state.preferences.preferredContinents,
                            onContinentsChanged = { viewModel.updatePreferredContinents(it) }
                        )
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Button(
                            onClick = { viewModel.savePreferences() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = !state.isLoading,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                            } else {
                                Icon(
                                    Icons.Default.Check, 
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                            Text(
                                text = if (isEditMode) stringResource(R.string.update_preferences) else stringResource(
                                    R.string.save_preferences
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (isEditMode) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { viewModel.showClearConfirmationDialog() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                enabled = !state.isLoading,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error)
                                )
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = stringResource(R.string.clear_all_responses),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        if (state.errorMessage != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = state.errorMessage!!,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (state.showClearConfirmation) {
            AlertDialog(
                onDismissRequest = { viewModel.hideClearConfirmationDialog() },
                icon = {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                title = {
                    Text(
                        text = stringResource(R.string.confirm_clear_preferences),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.clear_preferences_description),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.clearPreferences() },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.yes_clear),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.hideClearConfirmationDialog() }
                    ) {
                        Text(stringResource(R.string.keep_preferences))
                    }
                }
            )
        }
    }
}



