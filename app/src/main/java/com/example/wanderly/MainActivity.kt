// MainActivity.kt
package com.example.wanderly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.wanderly.ui.theme.WanderlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WanderlyTheme {
                var showWelcome by remember { mutableStateOf(true) }
                var isAuthenticated by remember { mutableStateOf(false) }

                when {
                    showWelcome -> WelcomeScreen {
                        showWelcome = false
                    }
                    !isAuthenticated -> AuthScreen {
                        isAuthenticated = true
                    }
                    else -> AppNavigation()
                }
            }
        }
    }
}
