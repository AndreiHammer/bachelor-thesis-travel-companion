package eu.ase.travelcompanionapp.app

import android.app.Application
import com.google.android.libraries.places.api.Places
import eu.ase.travelcompanionapp.BuildConfig
import eu.ase.travelcompanionapp.core.data.localDB.DatabaseManager
import eu.ase.travelcompanionapp.core.data.localDB.DebugHelper
import eu.ase.travelcompanionapp.di.authModule
import eu.ase.travelcompanionapp.di.userModule
import eu.ase.travelcompanionapp.di.coreModule
import eu.ase.travelcompanionapp.di.databaseModule
import eu.ase.travelcompanionapp.di.hotelAmadeusModule
import eu.ase.travelcompanionapp.di.hotelPlacesModule
import eu.ase.travelcompanionapp.di.hotelSharedModule
import io.objectbox.BoxStore
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TravelCompanionApp: Application() {

    private val boxStore: BoxStore
        get() = DatabaseManager.boxStore

    override fun onCreate() {
        super.onCreate()

        DatabaseManager.initialize(this)
        DebugHelper.initialize(this, boxStore)

        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(applicationContext, BuildConfig.GOOGLE_API_KEY)
        }

        startKoin {
            androidContext(this@TravelCompanionApp)
            androidLogger()
            modules(
                listOf(
                    coreModule,
                    databaseModule,
                    authModule,
                    userModule,
                    hotelPlacesModule,
                    hotelAmadeusModule,
                    hotelSharedModule
                )
            )
        }
    }
}