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
        routePath("HotelRoute.HotelOffers"),
        routePath("HotelRoute.MapSearch")
    )

    return currentRoute in mainRoutes ||
            currentRoute?.startsWith(routePath("HotelRoute.HotelListCity")) == true ||
            currentRoute?.startsWith(routePath("HotelRoute.HotelListLocation")) == true ||
            currentRoute?.startsWith(routePath("HotelRoute.HotelDetail")) == true ||
            currentRoute?.startsWith(routePath("HotelRoute.HotelOffers")) == true ||
            currentRoute?.startsWith(routePath("HotelRoute.MapSearch")) == true
}