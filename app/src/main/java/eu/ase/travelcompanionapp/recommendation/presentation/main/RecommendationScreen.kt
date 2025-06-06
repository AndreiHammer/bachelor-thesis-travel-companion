package eu.ase.travelcompanionapp.recommendation.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.recommendation.presentation.main.components.QuestionnaireCompletedCard
import eu.ase.travelcompanionapp.recommendation.presentation.main.components.QuestionnairePromptCard
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.QuestionnaireDialog
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.QuestionnaireViewModel
import eu.ase.travelcompanionapp.ui.CompanionTopAppBar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationScreen(
    navController: NavController,
    viewModel: RecommendationViewModel
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
                        state = state,
                        onEditPreferences = { 
                            isEditMode = true
                            showQuestionnaireDialog = true 
                        },
                        onSendProfile = { viewModel.sendUserProfileToApi() },
                        onClearMessages = { viewModel.clearMessages() },
                        onGetRecommendations = { viewModel.getRecommendations() },
                        onViewRecommendations = {
                            viewModel.navigateToDestinationList()
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

