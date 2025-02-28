package eu.ase.travelcompanionapp.app

import android.app.Application
import com.google.android.libraries.places.api.Places
import eu.ase.travelcompanionapp.BuildConfig
import eu.ase.travelcompanionapp.core.data.localDB.MyObjectBox
import eu.ase.travelcompanionapp.di.authModule
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
    lateinit var boxStore: BoxStore
        private set

    override fun onCreate() {
        super.onCreate()

        boxStore = MyObjectBox.builder()
            .androidContext(this)
            .build()

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
                    hotelPlacesModule,
                    hotelAmadeusModule,
                    hotelSharedModule
                )
            )
        }
    }
}