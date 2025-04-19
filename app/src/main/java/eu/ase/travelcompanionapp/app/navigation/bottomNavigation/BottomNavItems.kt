package eu.ase.travelcompanionapp.app.navigation.bottomNavigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart

object BottomNavItems {
    val items = listOf(
        BottomNavItem(
            label = "Search",
            icon = Icons.Filled.Search,
            route = routePath("HotelRoute.LocationSearch")
        ),
        BottomNavItem(
            label = "Favourites",
            icon = Icons.Filled.Favorite,
            route = routePath("HotelRoute.Favourites")
        ),
        BottomNavItem(
            label = "Bookings",
            icon = Icons.Filled.ShoppingCart,
            route = routePath("PaymentRoute.BookingHistory")
        ),
        BottomNavItem(
            label = "Profile",
            icon = Icons.Filled.Person,
            route = routePath("ProfileRoute.Profile")
        )
    )
}