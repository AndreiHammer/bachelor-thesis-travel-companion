package eu.ase.travelcompanionapp.app.navigation.bottomNavigation

import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.koin.androidx.compose.koinViewModel

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    viewModel: BottomNavigationViewModel = koinViewModel()
) {

    val selectedIconColor = MaterialTheme.colorScheme.onPrimary
    val unselectedIconColor = MaterialTheme.colorScheme.onSurface
    val selectedTextColor = MaterialTheme.colorScheme.onPrimary
    val unselectedTextColor = MaterialTheme.colorScheme.onSurface
    val navigationBarColor = MaterialTheme.colorScheme.primary

    val count by viewModel.favouriteCount.collectAsStateWithLifecycle()

    Surface(
        color = navigationBarColor,
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            containerColor = navigationBarColor
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            BottomNavItems.items.forEach { navItem ->
                val isSelected = currentRoute?.startsWith(navItem.route.substringBefore('/')) == true
                
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        when (navItem.label) {
                            "Search" -> {
                                navController.navigate(routePath("HotelRoute.LocationSearch")) {
                                    popUpTo(routePath("RootRoute.RootGraph")) {
                                        inclusive = false
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            "Profile" -> {
                                navController.navigate(routePath("ProfileRoute.Profile")) {
                                    popUpTo(routePath("RootRoute.RootGraph")) {
                                        inclusive = false
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            "Favourites" -> {
                                viewModel.loadFavouriteCount()
                                navController.navigate(routePath("HotelRoute.Favourites")) {
                                    popUpTo(routePath("RootRoute.RootGraph")) {
                                        inclusive = false
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    },
                    icon = {
                        if (navItem.label == "Favourites" && count > 0) {
                            BadgedBox(
                                badge = {
                                    Badge {
                                        Text(text = count.toString())
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = navItem.icon,
                                    contentDescription = navItem.label,
                                    tint = if (isSelected) selectedIconColor else unselectedIconColor
                                )
                            }
                        } else {
                            Icon(
                                imageVector = navItem.icon,
                                contentDescription = navItem.label,
                                tint = if (isSelected) selectedIconColor else unselectedIconColor
                            )
                        }
                    },
                    label = {
                        Text(
                            text = navItem.label,
                            color = if (isSelected) selectedTextColor else unselectedTextColor
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = selectedIconColor,
                        unselectedIconColor = unselectedIconColor,
                        selectedTextColor = selectedTextColor,
                        unselectedTextColor = unselectedTextColor,
                        indicatorColor = MaterialTheme.colorScheme.secondary
                    ),
                    alwaysShowLabel = true
                )
            }
        }
    }
}