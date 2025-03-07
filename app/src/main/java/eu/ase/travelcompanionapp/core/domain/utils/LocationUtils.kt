package eu.ase.travelcompanionapp.core.domain.utils

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng

class LocationUtils {

    fun getUserLocation(context: Context, onLocationResult: (Location?) -> Unit) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            onLocationResult(null)
            return
        }

        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (location != null) {
            onLocationResult(location)
        } else {
            val locationListener = object : LocationListener {
                override fun onLocationChanged(loc: Location) {
                    onLocationResult(loc)
                    locationManager.removeUpdates(this)
                }

                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            try {
                locationManager.requestSingleUpdate(
                    LocationManager.GPS_PROVIDER,
                    locationListener,
                    null
                )
            } catch (e: Exception) {
                try {
                    locationManager.requestSingleUpdate(
                        LocationManager.NETWORK_PROVIDER,
                        locationListener,
                        null
                    )
                } catch (e: Exception) {
                    onLocationResult(null)
                }
            }
        }
    }
    fun locationToLatLng(location: Location): LatLng {
        return LatLng(location.latitude, location.longitude)
    }
} 