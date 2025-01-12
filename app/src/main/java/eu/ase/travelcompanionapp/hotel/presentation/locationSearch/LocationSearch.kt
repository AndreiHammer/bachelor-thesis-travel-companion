package eu.ase.travelcompanionapp.hotel.presentation.locationSearch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchScreen(
    modifier: Modifier = Modifier,
    onAction: (LocationSearchAction) -> Unit
) {
    val viewModel: LocationSearchViewModel = koinViewModel()

    val city = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Search") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "User Profile",
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                onAction(LocationSearchAction.OnProfileClick)
                            },
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                },
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAction(LocationSearchAction.OnMapClick)
                }
            ) {
                Icon(imageVector = Icons.Filled.LocationOn, contentDescription = "Map")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(text = "Filter your preferences:", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = city.value,
                onValueChange = { newCity ->
                    city.value = newCity
                    viewModel.onCityChange(newCity)
                },
                placeholder = {
                    Text(text = stringResource(R.string.enter_city))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.LightGray, shape = MaterialTheme.shapes.medium)
                    .padding(16.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onAction(LocationSearchAction.OnSearchClick(city.value))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Search Hotels in ${city.value}")
            }
        }
    }
}