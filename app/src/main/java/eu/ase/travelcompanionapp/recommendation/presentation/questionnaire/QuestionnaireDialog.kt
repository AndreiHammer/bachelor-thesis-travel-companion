package eu.ase.travelcompanionapp.recommendation.presentation.questionnaire

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.components.AccommodationTypeQuestion
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.components.BudgetRangeQuestion
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.components.GroupSizeQuestion
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.components.ImportanceRatingsSection
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.components.LocationPreferenceQuestion
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.components.PreferredAmenitiesQuestion
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.components.PreferredContinentsQuestion
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.components.TravelPurposeQuestion

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
                        BudgetRangeQuestion(
                            selectedBudget = state.preferences.budgetRange,
                            onBudgetSelected = { viewModel.updateBudgetRange(it) }
                        )
                    }

                    item {
                        TravelPurposeQuestion(
                            selectedPurpose = state.preferences.travelPurpose,
                            onPurposeSelected = { viewModel.updateTravelPurpose(it) }
                        )
                    }

                    item {
                        GroupSizeQuestion(
                            selectedSize = state.preferences.groupSize,
                            onSizeSelected = { viewModel.updateGroupSize(it) }
                        )
                    }

                    item {
                        AccommodationTypeQuestion(
                            selectedType = state.preferences.accommodationType,
                            onTypeSelected = { viewModel.updateAccommodationType(it) }
                        )
                    }

                    item {
                        LocationPreferenceQuestion(
                            selectedLocation = state.preferences.locationPreference,
                            onLocationSelected = { viewModel.updateLocationPreference(it) }
                        )
                    }

                    item {
                        ImportanceRatingsSection(
                            priceImportance = state.preferences.importanceFactors.price,
                            ratingImportance = state.preferences.importanceFactors.hotelRating,
                            amenitiesImportance = state.preferences.importanceFactors.amenities,
                            locationImportance = state.preferences.importanceFactors.location,
                            onPriceImportanceChanged = { viewModel.updatePriceImportance(it) },
                            onRatingImportanceChanged = { viewModel.updateRatingImportance(it) },
                            onAmenitiesImportanceChanged = { viewModel.updateAmenitiesImportance(it) },
                            onLocationImportanceChanged = { viewModel.updateLocationImportance(it) }
                        )
                    }

                    item {
                        PreferredAmenitiesQuestion(
                            selectedAmenities = state.preferences.importantAmenities,
                            onAmenitiesChanged = { viewModel.updatePreferredAmenities(it) }
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
    }
}



