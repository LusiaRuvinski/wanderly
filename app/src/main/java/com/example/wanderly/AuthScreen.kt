package com.example.wanderly

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(onAuthSuccess: () -> Unit){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUpMode by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isSignUpMode) "Sign Up" else "Login",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isSignUpMode) {
                    signUpWithEmailAndPassword(email, password) { success ->
                        if (success) onAuthSuccess()
                        else errorMessage = "Sign up failed"
                    }
                } else {
                    signInWithEmailAndPassword(email, password) { success ->
                        if (success) onAuthSuccess()
                        else errorMessage = "Login failed"
                    }
                }
            }
        ) {
            Text(if (isSignUpMode) "Sign Up" else "Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { isSignUpMode = !isSignUpMode }) {
            Text(
                if (isSignUpMode) "Already have an account? Log in"
                else "Don't have an account? Sign up"
            )
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}
