package eu.ase.travelcompanionapp.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import eu.ase.travelcompanionapp.app.navigation.graphs.AuthGraph
import eu.ase.travelcompanionapp.app.navigation.graphs.DestinationGraph
import eu.ase.travelcompanionapp.app.navigation.graphs.HotelGraph
import eu.ase.travelcompanionapp.app.navigation.graphs.PaymentGraph
import eu.ase.travelcompanionapp.app.navigation.graphs.ProfileGraph
import eu.ase.travelcompanionapp.app.navigation.graphs.RootGraph
import eu.ase.travelcompanionapp.app.navigation.graphs.TouristAttractionsGraph
import eu.ase.travelcompanionapp.app.navigation.routes.RootRoute
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.ParametersDefinition

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
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

        DestinationGraph(navController = navController)

        PaymentGraph(navController = navController)

        TouristAttractionsGraph(navController = navController)
    }
}


@Composable
inline fun <reified T: ViewModel> NavBackStackEntry.sharedKoinViewModel(
    navController: NavController,
    noinline parameters: ParametersDefinition? = null
): T {
    val navGraphRoute = destination.parent?.route ?: return koinViewModel<T>()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return koinViewModel(
        viewModelStoreOwner = parentEntry,
        parameters = parameters
    )
}

