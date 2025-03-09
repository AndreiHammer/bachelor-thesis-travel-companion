package eu.ase.travelcompanionapp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import eu.ase.travelcompanionapp.app.navigation.AppNavHost
import eu.ase.travelcompanionapp.app.navigation.bottomNavigation.BottomNavigationBar
import eu.ase.travelcompanionapp.app.navigation.bottomNavigation.showBottomBar
import eu.ase.travelcompanionapp.ui.theme.TravelCompanionAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            TravelCompanionAppTheme {
                val navController = rememberNavController()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val currentRoute = currentDestination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar(currentRoute)) {
                            BottomNavigationBar(navController = navController)
                        }
                    }, 
                    content = { innerPadding ->
                        AppNavHost(
                            modifier = Modifier.padding(innerPadding),
                            navController = navController
                        )
                    }
                )
            }
        }
    }
}