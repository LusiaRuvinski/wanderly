package com.example.wanderly

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun AddTripScreen(
    onTripAdded: () -> Unit,
    onBackToMenu: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(TextFieldValue("")) }
    var destination by remember { mutableStateOf(TextFieldValue("")) }
    var startDate by remember { mutableStateOf(TextFieldValue("")) }
    var endDate by remember { mutableStateOf(TextFieldValue("")) }
    var isSaving by remember { mutableStateOf(false) }

    val purpleDark = Color(0xFF7B1FA2)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE1BEE7))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Add Trip",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("Destination") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                label = { Text("Start Date") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = endDate,
                onValueChange = { endDate = it },
                label = { Text("End Date") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.text.isBlank() || destination.text.isBlank() ||
                        startDate.text.isBlank() || endDate.text.isBlank()
                    ) {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser == null) {
                        Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isSaving = true

                    val trip = hashMapOf(
                        "name" to name.text,
                        "destination" to destination.text,
                        "startDate" to startDate.text,
                        "endDate" to endDate.text,
                        "userId" to currentUser.uid
                    )

                    Firebase.firestore.collection("trips")
                        .add(trip)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Trip added successfully", Toast.LENGTH_SHORT).show()
                            onTripAdded()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to add trip", Toast.LENGTH_SHORT).show()
                            isSaving = false
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = purpleDark)
            ) {
                Text("Add", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBackToMenu,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = purpleDark)
            ) {
                Text("Back to Menu", color = Color.White)
            }
        }
    }
}
