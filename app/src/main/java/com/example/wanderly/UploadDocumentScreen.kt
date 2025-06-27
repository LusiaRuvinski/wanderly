package com.example.wanderly

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun UploadDocumentScreen(tripId: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val firestore = Firebase.firestore
    val currentUser = FirebaseAuth.getInstance().currentUser
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var customFileName by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Wanderly",
                style = MaterialTheme.typography.headlineMedium
            )

            Icon(
                imageVector = Icons.Default.CloudUpload,
                contentDescription = "Upload Icon",
                tint = Color(0xFF407BFF),
                modifier = Modifier.size(100.dp)
            )

            Button(
                onClick = { filePicker.launch("*/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF407BFF))
            ) {
                Text("Choose File")
            }

            selectedFileUri?.let { uri ->
                Text("Selected: ${uri.lastPathSegment}", color = Color.DarkGray)

                OutlinedTextField(
                    value = customFileName,
                    onValueChange = { customFileName = it },
                    label = { Text("Enter a file name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (currentUser == null) {
                            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isUploading = true
                        val file = uriToFile(context, uri)
                        val finalFileName = if (customFileName.isNotBlank()) customFileName else file.name

                        CloudinaryUploader.uploadFile(file) { success, url ->
                            if (success && url != null) {
                                val fileData = mapOf(
                                    "tripId" to tripId,
                                    "fileName" to finalFileName,
                                    "downloadUrl" to url,
                                    "timestamp" to System.currentTimeMillis(),
                                    "userId" to currentUser.uid
                                )
                                firestore.collection("tripDocuments")
                                    .add(fileData)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Upload successful", Toast.LENGTH_SHORT).show()
                                        selectedFileUri = null
                                        customFileName = ""
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Failed to save metadata", Toast.LENGTH_SHORT).show()
                                    }
                                    .also { isUploading = false }
                            } else {
                                Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
                                isUploading = false
                            }
                        }
                    },
                    enabled = !isUploading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF407BFF))
                ) {
                    Text("Upload")
                }
            }

            if (isUploading) {
                CircularProgressIndicator()
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBack) {
                Text("Back", color = Color.Gray)
            }
        }
    }
}
