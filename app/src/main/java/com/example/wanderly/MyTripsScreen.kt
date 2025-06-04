package com.example.wanderly

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class Trip(
    val id: String = "",
    val name: String = "",
    val destination: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val note: String = ""
)

@Composable
fun MyTripsScreen(
    onBackToMenu: () -> Unit,
    onTripClick: (String) -> Unit
) {
    val db = Firebase.firestore
    val context = LocalContext.current
    var trips by remember { mutableStateOf<List<Trip>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var tripToDelete by remember { mutableStateOf<Trip?>(null) }

    LaunchedEffect(Unit) {
        db.collection("trips")
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
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFEBEE)) // רקע ורוד בייבי
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.Magenta)
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "My Trips",
                    fontSize = 30.sp,
                    color = Color(0xFFD81B60),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(trips) { trip ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { onTripClick(trip.id) }, // מעבר לפרטי טיול
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("📍 ${trip.name}", fontSize = 18.sp)
                                Text("Destination: ${trip.destination}")
                                Text("Start: ${trip.startDate}")
                                Text("End: ${trip.endDate}")
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        tripToDelete = trip
                                        showDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD81B60))
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Delete", color = Color.White)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onBackToMenu,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD81B60))
                ) {
                    Text("Back to Menu", color = Color.White)
                }
            }
        }
    }

    if (showDialog && tripToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this trip?") },
            confirmButton = {
                TextButton(onClick = {
                    tripToDelete?.let { trip ->
                        db.collection("trips").document(trip.id)
                            .delete()
                            .addOnSuccessListener {
                                trips = trips.filter { it.id != trip.id }
                                Toast.makeText(context, "Trip deleted successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to delete trip", Toast.LENGTH_SHORT).show()
                            }
                    }
                    showDialog = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
