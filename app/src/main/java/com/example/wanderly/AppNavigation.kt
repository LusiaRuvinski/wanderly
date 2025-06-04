package com.example.wanderly

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "menu") {

        composable("menu") {
            MenuScreen(onNavigate = { route ->
                navController.navigate(route)
            })
        }

        composable("profile") {
            ProfileScreen(onBackToMenu = {
                navController.navigate("menu") {
                    popUpTo("menu") { inclusive = true }
                }
            })
        }

        composable("addTrip") {
            AddTripScreen(onTripAdded = {
                navController.navigate("menu") {
                    popUpTo("menu") { inclusive = true }
                }
            })
        }

        composable("trips") {
            MyTripsScreen(
                onBackToMenu = {
                    navController.navigate("menu") {
                        popUpTo("menu") { inclusive = true }
                    }
                },
                onTripClick = { tripId ->
                    navController.navigate("tripDetails/$tripId")
                }
            )
        }

        composable(
            "tripDetails/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: return@composable
            TripDetailsScreen(
                tripId = tripId,
                onBack = { navController.popBackStack() },
                onOpenMap = { destination ->
                    navController.navigate("map/$destination")
                },
                onOpenFiles = { tripId ->
                    navController.navigate("tripFiles/$tripId")
                }
            )
        }

        composable(
            "map/{destination}",
            arguments = listOf(navArgument("destination") { type = NavType.StringType })
        ) { backStackEntry ->
            val dest = backStackEntry.arguments?.getString("destination") ?: ""
            TripMapScreen(destination = dest) {
                navController.popBackStack()
            }
        }

        // מסך בחירת טיול לצורך העלאה
        composable("upload") {
            SelectTripForUploadScreen(
                onTripSelected = { tripId ->
                    navController.navigate("upload/$tripId")
                },
                onBack = {
                    navController.navigate("menu") {
                        popUpTo("menu") { inclusive = true }
                    }
                }
            )
        }

        // מסך העלאת קובץ לטיול מסוים
        composable(
            "upload/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            UploadDocumentScreen(tripId = tripId) {
                navController.popBackStack()
            }
        }

        // מסך צפייה בקבצים שהועלו לטיול מסוים
        composable(
            "tripFiles/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            TripFilesScreen(tripId = tripId) {
                navController.popBackStack()
            }
        }
    }
}
