package eu.ase.travelcompanionapp.user.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.ui.CompanionTopAppBar
import eu.ase.travelcompanionapp.user.presentation.settings.components.CurrencyPreferenceCard
import eu.ase.travelcompanionapp.user.presentation.settings.components.ThemePreferenceCard
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val currencies by viewModel.currencies.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CompanionTopAppBar(
                title = stringResource(R.string.settings),
                onNavigationClick = { viewModel.handleAction(SettingsAction.OnBackClick) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            ThemePreferenceCard(
                isDarkTheme = isDarkTheme,
                onThemeChanged = { darkTheme ->
                    viewModel.updateThemePreference(darkTheme)
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CurrencyPreferenceCard(
                currencies = currencies,
                selectedCurrency = selectedCurrency,
                onCurrencySelected = { currencyCode -> 
                    viewModel.updatePreferredCurrency(currencyCode)
                }
            )
        }
    }
}


