package eu.ase.travelcompanionapp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import eu.ase.travelcompanionapp.app.navigation.AppNavHost
import eu.ase.travelcompanionapp.app.navigation.bottomNavigation.BottomNavigationBar
import eu.ase.travelcompanionapp.app.navigation.bottomNavigation.showBottomBar
import eu.ase.travelcompanionapp.ui.ThemeManager
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val themeManager: ThemeManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            themeManager.ApplyTheme {
                val navController = rememberNavController()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val currentRoute = currentDestination?.route
                
                Box(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                bottom = if (showBottomBar(currentRoute)) 80.dp else 0.dp
                            ),
                        navController = navController
                    )
                
                    if (showBottomBar(currentRoute)) {
                        BottomNavigationBar(
                            navController = navController,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
    }
}