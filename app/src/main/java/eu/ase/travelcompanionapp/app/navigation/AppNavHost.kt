package eu.ase.travelcompanionapp.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import eu.ase.travelcompanionapp.app.navigation.graphs.AuthGraph
import eu.ase.travelcompanionapp.app.navigation.graphs.HotelGraph
import eu.ase.travelcompanionapp.app.navigation.graphs.ProfileGraph
import eu.ase.travelcompanionapp.app.navigation.graphs.RootGraph
import eu.ase.travelcompanionapp.app.navigation.routes.RootRoute
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = RootRoute.RootGraph,
        modifier = modifier
    ) {

        RootGraph(navController = navController)

        AuthGraph(navController = navController, context = context)

        ProfileGraph(navController = navController)

        HotelGraph(navController = navController)
    }
}


@Composable
inline fun <reified T: ViewModel> NavBackStackEntry.sharedKoinViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return koinViewModel<T>()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return koinViewModel(
        viewModelStoreOwner = parentEntry
    )
}

