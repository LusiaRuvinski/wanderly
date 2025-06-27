package com.example.wanderly

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun TripDetailsScreen(
    tripId: String,
    onBack: () -> Unit,
    onOpenMap: (String) -> Unit,
    onOpenFiles: (String) -> Unit
) {
    val context = LocalContext.current
    val db = Firebase.firestore

    var trip by remember { mutableStateOf<Trip?>(null) }
    var note by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var fileUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var fileLinks by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(tripId) {
        db.collection("trips").document(tripId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    trip = Trip(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        destination = doc.getString("destination") ?: "",
                        startDate = doc.getString("startDate") ?: "",
                        endDate = doc.getString("endDate") ?: "",
                        note = doc.getString("note") ?: ""
                    )
                    note = trip?.note ?: ""
                }
                isLoading = false
            }

        db.collection("tripDocuments")
            .whereEqualTo("tripId", tripId)
            .get()
            .addOnSuccessListener { docs ->
                val links = docs.mapNotNull { it.getString("downloadUrl") }
                fileLinks = links
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8E1))
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            trip?.let {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                        .padding(bottom = 160.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Trip Details", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Destination: ${it.destination}", fontSize = 20.sp)
                    Text("Start Date: ${it.startDate}", fontSize = 18.sp)
                    Text("End Date: ${it.endDate}", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Notes") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 150.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            db.collection("trips").document(tripId)
                                .update("note", note)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Note saved successfully", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Failed to save note", Toast.LENGTH_SHORT).show()
                                }
                        }
                    ) {
                        Text("Save Note")
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            Spacer(Modifier.width(6.dp))
                            Text("Back")
                        }

                        Button(onClick = {
                            onOpenMap(it.destination)
                        }) {
                            Icon(Icons.Default.Map, contentDescription = "Map")
                            Spacer(Modifier.width(6.dp))
                            Text("Map")
                        }

                        Button(onClick = {
                            onOpenFiles(it.id)
                        }) {
                            Icon(Icons.Default.AttachFile, contentDescription = "Files")
                            Spacer(Modifier.width(6.dp))
                            Text("Files")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val shareText = buildString {
                                appendLine("Trip to: ${it.destination}")
                                appendLine("From: ${it.startDate} To: ${it.endDate}")
                                appendLine("\nNotes:\n$note")
                                if (fileLinks.isNotEmpty()) {
                                    appendLine("\nFiles:")
                                    fileLinks.forEach { url -> appendLine(url) }
                                }
                            }

                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "My Trip to ${it.destination}")
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }

                            context.startActivity(Intent.createChooser(intent, "Share trip via"))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                        Spacer(Modifier.width(8.dp))
                        Text("Share Trip")
                    }
                }
            } ?: Text("Trip not found", modifier = Modifier.align(Alignment.Center))
        }
    }
}
