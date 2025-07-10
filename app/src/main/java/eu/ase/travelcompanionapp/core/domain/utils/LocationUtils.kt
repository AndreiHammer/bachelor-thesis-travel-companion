package eu.ase.travelcompanionapp.core.domain.utils

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationUtils {

    @RequiresApi(Build.VERSION_CODES.R)
    fun getUserLocation(context: Context, onLocationResult: (Location?) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val location = getLocation(context)
            onLocationResult(location)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private suspend fun getLocation(context: Context): Location? {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED) {
            return null
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (lastLocation != null) {
            return lastLocation
        }

        return suspendCancellableCoroutine { continuation ->
            val cancellationSignal = CancellationSignal()

            continuation.invokeOnCancellation { cancellationSignal.cancel() }

            try {
                locationManager.getCurrentLocation(
                    LocationManager.GPS_PROVIDER,
                    cancellationSignal,
                    context.mainExecutor
                ) { location ->
                    if (location != null) {
                        continuation.resume(location)
                    } else {
                        locationManager.getCurrentLocation(
                            LocationManager.NETWORK_PROVIDER,
                            cancellationSignal,
                            context.mainExecutor
                        ) { networkLocation ->
                            continuation.resume(networkLocation)
                        }
                    }
                }
            } catch (e: Exception) {
                continuation.resume(null)
            }
        }
    }

    fun locationToLatLng(location: Location): LatLng {
        return LatLng(location.latitude, location.longitude)
    }
}