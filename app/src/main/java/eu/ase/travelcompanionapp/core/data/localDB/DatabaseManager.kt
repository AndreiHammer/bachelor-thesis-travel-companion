package eu.ase.travelcompanionapp.core.data.localDB

import android.content.Context
import io.objectbox.BoxStore

object DatabaseManager {
    
    private var _boxStore: BoxStore? = null

    val boxStore: BoxStore
        get() = _boxStore ?: throw IllegalStateException("Database not initialized. Call initialize() first.")

    fun initialize(context: Context): BoxStore {
        if (_boxStore == null) {
            _boxStore = MyObjectBox.builder()
                .androidContext(context)
                .build()
        }
        return _boxStore!!
    }
} 