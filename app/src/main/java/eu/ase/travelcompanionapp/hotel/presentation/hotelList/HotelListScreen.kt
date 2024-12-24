package eu.ase.travelcompanionapp.hotel.presentation.hotelList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.ase.travelcompanionapp.core.presentation.BlurredAnimatedText
import eu.ase.travelcompanionapp.hotel.domain.Hotel
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.components.HotelList
import org.koin.androidx.compose.koinViewModel


@Composable
fun HotelListScreenRoot(
    viewModel: HotelListViewModel = koinViewModel(),
    onHotelClick: (Hotel) -> Unit,
    modifier: Modifier = Modifier
){
    val state by viewModel.hotelState.collectAsStateWithLifecycle()

    HotelListScreen(
        viewModel = viewModel,
        state = state,
        onAction = { action ->
            when(action){
                is HotelListAction.OnHotelClick -> onHotelClick(action.hotel)
                else -> Unit
            }
            viewModel.onAction(action)
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelListScreen(
    viewModel: HotelListViewModel,
    modifier: Modifier = Modifier,
    state: HotelListViewModel.HotelListState,
    onAction: (HotelListAction) -> Unit,
    ) {

    val hotelState by viewModel.hotelState.collectAsState()


    viewModel.getHotelList(city = "BUH", amenities = "", rating = "5")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Hotels List - Bucharest") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (hotelState.isLoading) {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    BlurredAnimatedText(text = "Loading hotels...")
                }
            } else {
                HotelList(
                    hotels = hotelState.hotels,
                    onHotelClick = { hotel ->
                        onAction(HotelListAction.OnHotelClick(hotel))
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
