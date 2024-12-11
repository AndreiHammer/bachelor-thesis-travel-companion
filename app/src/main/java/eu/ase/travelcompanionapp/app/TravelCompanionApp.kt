package eu.ase.travelcompanionapp.app

import android.app.Application
import com.google.android.libraries.places.api.Places
import eu.ase.travelcompanionapp.BuildConfig
import eu.ase.travelcompanionapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TravelCompanionApp: Application() {
    override fun onCreate() {
        super.onCreate()

        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(applicationContext, BuildConfig.GOOGLE_API_KEY)
        }


        startKoin {
            androidContext(this@TravelCompanionApp)
            androidLogger()
            modules(appModule)
        }
    }
}