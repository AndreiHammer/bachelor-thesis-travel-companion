package eu.ase.travelcompanionapp.core.domain.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object FavoritesEventBus {
    private val _events = MutableSharedFlow<FavoriteEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<FavoriteEvent> = _events.asSharedFlow()

    suspend fun emitEvent(event: FavoriteEvent) {
        _events.emit(event)
    }
}

sealed class FavoriteEvent {
    data object CountChanged : FavoriteEvent()
}