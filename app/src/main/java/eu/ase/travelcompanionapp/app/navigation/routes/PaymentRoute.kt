package eu.ase.travelcompanionapp.app.navigation.routes

import kotlinx.serialization.Serializable

sealed interface PaymentRoute {
    @Serializable
    data object PaymentGraph : PaymentRoute

    @Serializable
    data object Payment : PaymentRoute
    
    @Serializable
    data object BookingHistory : PaymentRoute
}