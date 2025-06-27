package com.example.wanderly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.wanderly.ui.theme.WanderlyTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WanderlyTheme {
                var showWelcome by remember { mutableStateOf(true) }

                var isAuthenticated by remember {
                    mutableStateOf(FirebaseAuth.getInstance().currentUser != null)
                }

                when {
                    showWelcome -> WelcomeScreen {
                        showWelcome = false
                    }
                    !isAuthenticated -> AuthScreen {
                        isAuthenticated = true
                    }
                    else -> AppNavigation(
                        onLogout = { isAuthenticated = false }
                    )
                }
            }
        }
    }
}
