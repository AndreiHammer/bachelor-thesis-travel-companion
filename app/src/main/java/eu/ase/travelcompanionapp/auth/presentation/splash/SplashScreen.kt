package eu.ase.travelcompanionapp.auth.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.core.presentation.BlurredAnimatedText
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

private const val SPLASH_TIMEOUT = 1000L

@Composable
fun SplashScreen(
    onUserKnown: () -> Unit,
    onUserUnknown: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = koinViewModel()
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        BlurredAnimatedText(text = stringResource(R.string.app_name))
    }


    LaunchedEffect(true) {
        delay(SPLASH_TIMEOUT)
        viewModel.onAppStart(
            onUserKnown = {
                onUserKnown()
            },
            onUserUnknown = {
                onUserUnknown()
            }
        )

    }
}