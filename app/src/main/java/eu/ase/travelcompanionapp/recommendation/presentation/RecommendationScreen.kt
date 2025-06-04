package eu.ase.travelcompanionapp.recommendation.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.QuestionnaireDialog
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.QuestionnaireViewModel
import eu.ase.travelcompanionapp.ui.CompanionTopAppBar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationScreen(
    navController: NavController,
    viewModel: RecommendationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    
    var showQuestionnaireDialog by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CompanionTopAppBar(
                title = stringResource(R.string.destination_recommendations),
                onNavigationClick = { navController.popBackStack() },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp
                    )
                }
                
                !state.hasCompletedQuestionnaire -> {
                    QuestionnairePromptCard(
                        onStartQuestionnaire = { 
                            isEditMode = false
                            showQuestionnaireDialog = true 
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                state.hasCompletedQuestionnaire -> {
                    QuestionnaireCompletedCard(
                        message = state.showMessage,
                        onEditPreferences = { 
                            isEditMode = true
                            showQuestionnaireDialog = true 
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        if (showQuestionnaireDialog) {
            val questionnaireViewModel = koinViewModel<QuestionnaireViewModel>()
            
            LaunchedEffect(isEditMode) {
                questionnaireViewModel.initializeForMode(isEditMode)
            }
            
            QuestionnaireDialog(
                viewModel = questionnaireViewModel,
                isEditMode = isEditMode,
                onDismiss = { 
                    showQuestionnaireDialog = false
                    viewModel.onQuestionnaireCompleted()
                }
            )
        }
    }
}

@Composable
private fun QuestionnairePromptCard(
    onStartQuestionnaire: () -> Unit,
    modifier: Modifier = Modifier
) {
    FeatureCard(
        modifier = modifier,
        icon = R.drawable.baseline_question_mark_24,
        title = stringResource(R.string.get_personalized_destination_recommendations),
        subtitle = stringResource(R.string.discover_amazing_places_tailored_just_for_you),
        description = stringResource(R.string.answer_a_few_questions_about_your_travel_preferences_to_receive_personalized_destination_recommendations_based_on_your_preferences_saved_hotels_and_booking_history),
        primaryButtonText = stringResource(R.string.start_questionnaire),
        primaryButtonIcon = Icons.Default.PlayArrow,
        onPrimaryClick = onStartQuestionnaire,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

@Composable
private fun QuestionnaireCompletedCard(
    message: String?,
    onEditPreferences: () -> Unit,
    modifier: Modifier = Modifier
) {
    FeatureCard(
        modifier = modifier,
        title = stringResource(R.string.preferences_set),
        subtitle = stringResource(R.string.your_travel_style_is_configured),
        description = message ?: stringResource(R.string.your_travel_preferences_have_been_saved_successfully_we_ll_use_these_to_provide_you_with_the_best_destination_recommendations),
        primaryButtonText = stringResource(R.string.edit_preferences),
        primaryButtonIcon = Icons.Default.Edit,
        onPrimaryClick = onEditPreferences,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    )
}

@Composable
private fun FeatureCard(
    modifier: Modifier = Modifier,
    icon: Int? = null,
    title: String,
    subtitle: String,
    description: String,
    primaryButtonText: String,
    primaryButtonIcon: ImageVector,
    onPrimaryClick: () -> Unit,
    secondaryButtonText: String? = null,
    onSecondaryClick: (() -> Unit)? = null,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (icon != null) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = contentColor.copy(alpha = 0.1f)
                    )
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(16.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = contentColor
                )
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = contentColor.copy(alpha = 0.7f),
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onPrimaryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = primaryButtonIcon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = primaryButtonText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                if (secondaryButtonText != null && onSecondaryClick != null) {
                    OutlinedButton(
                        onClick = onSecondaryClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = secondaryButtonText,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
} 