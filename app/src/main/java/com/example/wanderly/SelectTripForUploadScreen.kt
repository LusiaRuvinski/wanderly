package com.example.wanderly

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun SelectTripForUploadScreen(
    onTripSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    val db = Firebase.firestore
    val currentUser = FirebaseAuth.getInstance().currentUser
    var trips by remember { mutableStateOf<List<Trip>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (currentUser != null) {
            db.collection("trips")
                .whereEqualTo("userId", currentUser.uid)
                .get()
                .addOnSuccessListener { result ->
                    trips = result.map { doc ->
                        Trip(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            destination = doc.getString("destination") ?: "",
                            startDate = doc.getString("startDate") ?: "",
                            endDate = doc.getString("endDate") ?: ""
                        )
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F8E9)) // רקע ירקרק
            .padding(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (trips.isEmpty()) {
            Text("No trips found.", modifier = Modifier.align(Alignment.Center))
        } else {
            Column {
                Text(
                    text = "Select a Trip",
                    fontSize = 24.sp,
                    color = Color(0xFF33691E),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                )

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(trips) { trip ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { onTripSelected(trip.id) },
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("📍 ${trip.name}", fontSize = 18.sp)
                                Text("Destination: ${trip.destination}")
                                Text("From: ${trip.startDate} To: ${trip.endDate}")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF689F38))
                ) {
                    Text("Back", color = Color.White)
                }
            }
        }
    }
}
