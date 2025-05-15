package eu.ase.travelcompanionapp.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.ui.theme.TravelCompanionAppTheme
import eu.ase.travelcompanionapp.user.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class ThemeManager(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    val isDarkTheme: Flow<Boolean> = userPreferencesRepository.isDarkTheme

    fun setDarkTheme(isDarkTheme: Boolean) {
        coroutineScope.launch {
            userPreferencesRepository.setDarkTheme(isDarkTheme)
        }
    }

    @Composable
    fun ApplyTheme(content: @Composable () -> Unit) {
        val isDarkTheme by userPreferencesRepository.isDarkTheme.collectAsState(initial = isSystemInDarkTheme())
        
        TravelCompanionAppTheme(
            darkTheme = isDarkTheme,
            content = content
        )
    }
}

enum class AppBarStyle {
    DEFAULT,  // Small, fixed top app bar
    PINNED    // Small, pinned app bar that doesn't respond to scroll
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanionTopAppBar(
    title: String,
    onNavigationClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable RowScope.() -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.surface,
    navigationIcon: @Composable () -> Unit = {
        IconButton(onClick = onNavigationClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back)
            )
        }
    },
    style: AppBarStyle = AppBarStyle.DEFAULT
) {
    val titleContent: @Composable () -> Unit = {
        Column(modifier = Modifier.padding(start = 4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
    
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = containerColor,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.primary
    )
    
    if (style == AppBarStyle.DEFAULT) {
        MediumTopAppBar(
            title = titleContent,
            navigationIcon = navigationIcon,
            actions = actions,
            colors = colors,
            scrollBehavior = scrollBehavior,
            modifier = modifier
        )
    } else {
        TopAppBar(
            title = titleContent,
            navigationIcon = navigationIcon,
            actions = actions,
            colors = colors,
            scrollBehavior = null,
            modifier = modifier
        )
    }
} 