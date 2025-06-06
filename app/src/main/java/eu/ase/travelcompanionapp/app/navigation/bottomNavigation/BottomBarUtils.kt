package eu.ase.travelcompanionapp.app.navigation.bottomNavigation

fun routePath(route: String): String {
    return "eu.ase.travelcompanionapp.app.navigation.routes.$route"
}

fun showBottomBar(currentRoute: String?): Boolean {
    val mainRoutes = listOf(
        routePath("HotelRoute.LocationSearch"),
        routePath("HotelRoute.HotelListCity"),
        routePath("HotelRoute.HotelListLocation"),
        routePath("HotelRoute.HotelDetail"),
        routePath("ProfileRoute.Profile"),
        routePath("ProfileRoute.Settings"),
        routePath("HotelRoute.HotelOffers"),
        routePath("HotelRoute.MapSearch"),
        routePath("HotelRoute.Favourites"),
        routePath("PaymentRoute.Payment"),
        routePath("PaymentRoute.BookingHistory"),
        routePath("DestinationRoute.Recommendations"),
        routePath("DestinationRoute.DestinationList"),
        routePath("TouristAttractionsRoute.TouristAttractionsList"),
        routePath("TouristAttractionsRoute.TouristAttractionDetails")
    )

    return currentRoute in mainRoutes ||
            currentRoute?.startsWith(routePath("HotelRoute.HotelListCity")) == true ||
            currentRoute?.startsWith(routePath("HotelRoute.HotelListLocation")) == true ||
            currentRoute?.startsWith(routePath("HotelRoute.HotelDetail")) == true ||
            currentRoute?.startsWith(routePath("HotelRoute.HotelOffers")) == true ||
            currentRoute?.startsWith(routePath("HotelRoute.MapSearch")) == true ||
            currentRoute?.startsWith(routePath("HotelRoute.Favourites")) == true ||
            currentRoute?.startsWith(routePath("ProfileRoute.Profile")) == true ||
            currentRoute?.startsWith(routePath("ProfileRoute.Settings")) == true ||
            currentRoute?.startsWith(routePath("PaymentRoute.Payment")) == true ||
            currentRoute?.startsWith(routePath("PaymentRoute.BookingHistory")) == true ||
            currentRoute?.startsWith(routePath("DestinationRoute.Recommendations")) == true ||
            currentRoute?.startsWith(routePath("DestinationRoute.DestinationList")) == true ||
            currentRoute?.startsWith(routePath("TouristAttractionsRoute.TouristAttractionsList")) == true ||
            currentRoute?.startsWith(routePath("TouristAttractionsRoute.TouristAttractionDetails")) == true
}