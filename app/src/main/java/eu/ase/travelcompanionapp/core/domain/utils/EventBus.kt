package eu.ase.travelcompanionapp.core.domain.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class EventBus<T> {
    private val _events = MutableSharedFlow<T>(extraBufferCapacity = 1)
    val events: SharedFlow<T> = _events.asSharedFlow()

    suspend fun emitEvent(event: T) {
        _events.emit(event)
    }

    companion object {
        val favourites = EventBus<FavouriteEvent>()
        val bookings = EventBus<BookingEvent>()
    }
}

sealed class FavouriteEvent {
    data object CountChanged : FavouriteEvent()
}

sealed class BookingEvent {
    data object CountChanged : BookingEvent()
} 