package eu.ase.travelcompanionapp.core.data.localDB

import android.content.Context
import android.util.Log
import eu.ase.travelcompanionapp.BuildConfig
import io.objectbox.BoxStore
import io.objectbox.android.Admin

object DebugHelper {

    fun initialize(context: Context, boxStore: BoxStore) {
        if (!BuildConfig.DEBUG) {
            return
        }

        initializeObjectBoxAdmin(context, boxStore)
    }

    private fun initializeObjectBoxAdmin(context: Context, boxStore: BoxStore) {
        try {
            val started = Admin(boxStore).start(context)
            Log.i("ObjectBoxAdmin", "Started: $started")
        } catch (e: Exception) {
            Log.e("ObjectBoxAdmin", "Error starting Admin: ${e.message}", e)
        }
    }

}