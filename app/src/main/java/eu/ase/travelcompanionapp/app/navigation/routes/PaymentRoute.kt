package eu.ase.travelcompanionapp.app.navigation.routes

import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.serialization.Serializable

sealed interface PaymentRoute {
    @Serializable
    data object PaymentGraph : PaymentRoute {
        override fun toString(): String = "payment_graph"
    }

    @Serializable
    data class Payment(
        val bookingReference: String = ""
    ) : PaymentRoute {
        fun createRoute() = "payment/$bookingReference"

        companion object {
            const val ROUTE = "payment/{bookingReference}"
            
            val paymentArguments = listOf(
                navArgument("bookingReference") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        }
    }
}