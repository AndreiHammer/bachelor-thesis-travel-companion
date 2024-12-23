package eu.ase.travelcompanionapp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import eu.ase.travelcompanionapp.core.data.HttpClientFactory

import eu.ase.travelcompanionapp.ui.theme.TravelCompanionAppTheme
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.runBlocking
import eu.ase.travelcompanionapp.core.domain.Result
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.network.AmadeusApiService
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.repository.AmadeusHotelRepository
import eu.ase.travelcompanionapp.hotel.domain.HotelRepositoryAmadeusApi


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            TravelCompanionAppTheme {
                /*Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    /*HotelLocationScreen(
                        country = "RO",
                        hotelName = "Sheraton Hotel Bucharest",
                        modifier = Modifier.padding(innerPadding)
                    )*/

                    /*HotelListScreen(
                        modifier = Modifier.padding(innerPadding)
                    )*/


                }*/

                val amadeusApi = AmadeusApiService(HttpClientFactory.create(CIO.create()))

                runBlocking {
                    val hotelRepository: HotelRepositoryAmadeusApi = AmadeusHotelRepository(amadeusApi)
                    hotelRepository.searchHotelsByCity("BUH", "", "5") { result ->
                        when (result) {

                            is Result.Error -> {
                                println("Error: ${result.error}")
                            }

                            is Result.Success -> {
                                result.data.forEach { hotel ->
                                    println("Hotel: $hotel")
                                }
                            }
                        }

                    }

                }
            }
        }
    }
}