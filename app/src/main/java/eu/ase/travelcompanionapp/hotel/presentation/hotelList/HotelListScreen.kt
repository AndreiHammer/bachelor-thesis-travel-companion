package eu.ase.travelcompanionapp.hotel.presentation.hotelList

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.core.presentation.BlurredAnimatedText
import eu.ase.travelcompanionapp.hotel.data.mappers.CityToIATAMapper
import eu.ase.travelcompanionapp.hotel.domain.Hotel
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.HotelLocationAction
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.components.HotelList
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.components.HotelListScreenError
import org.koin.androidx.compose.koinViewModel


@Composable
fun HotelListScreenRoot(
    viewModel: HotelListViewModel = koinViewModel(),
    onHotelClick: (Hotel) -> Unit,
    onBackClick: () -> Unit,
    selectedCity: String,
    context: Context,
    modifier: Modifier = Modifier
){
    val state by viewModel.hotelState.collectAsStateWithLifecycle()

    HotelListScreen(
        viewModel = viewModel,
        state = state,
        selectedCity = selectedCity,
        context = context,
        onAction = { action ->
            when(action){
                is HotelListAction.OnHotelClick -> onHotelClick(action.hotel)
                is HotelListAction.OnBackClick -> onBackClick()
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
    selectedCity: String,
    context: Context,
    onAction: (HotelListAction) -> Unit,
) {
    CityToIATAMapper.loadCityToIATAMap(context)
    val iataCode = CityToIATAMapper.getIATACode(selectedCity)

    if (iataCode == null) {
        HotelListScreenError(
            errorMessage = stringResource(R.string.invalid_city_error),
            onAction = { action ->
                when (action) {
                    is HotelListAction.OnBackClick -> onAction(action)
                    is HotelListAction.OnHotelClick -> {

                    }
                }
            }
        )
        return
    }



    viewModel.getHotelList(city = iataCode, amenities = "SWIMMING_POOL, AIR_CONDITIONING, WIFI, RESTAURANT", rating = "5")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Hotels List - $selectedCity") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars),
                navigationIcon = {
                    IconButton(onClick = { onAction(HotelListAction.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    BlurredAnimatedText(text = stringResource(R.string.loading_hotels))
                }
            } else {
                HotelList(
                    hotels = state.hotels,
                    onHotelClick = { hotel ->
                        onAction(HotelListAction.OnHotelClick(hotel))
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
