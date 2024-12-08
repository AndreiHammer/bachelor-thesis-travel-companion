package eu.ase.travelcompanionapp.app

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import eu.ase.travelcompanionapp.BuildConfig
import eu.ase.travelcompanionapp.ui.theme.TravelCompanionAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TravelCompanionAppTheme {
                // accesare API KEY prin cod
                // val apiKey = BuildConfig.GOOGLE_MAPS_API_KEY
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SingaporeMap(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SingaporeMap(modifier: Modifier = Modifier) {
    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(singapore, 10f)
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = com.google.maps.android.compose.MarkerState(position = singapore),
            title = "Singapore",
            snippet = "Welcome to Singapore!"
        )
    }
}

