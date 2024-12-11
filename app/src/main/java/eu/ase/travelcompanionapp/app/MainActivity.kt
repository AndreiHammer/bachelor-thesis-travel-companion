package eu.ase.travelcompanionapp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import eu.ase.travelcompanionapp.hotel.presentation.location.HotelLocationScreen
import eu.ase.travelcompanionapp.ui.theme.TravelCompanionAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            TravelCompanionAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HotelLocationScreen(
                        country = "RO",
                        latitude = 44.425761472937154,
                        longitude = 26.07691515766976,
                        hotelName = "JW Marriott Bucharest Grand Hotel",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}