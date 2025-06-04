package com.example.wanderly

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class TripFile(
    val documentId: String = "",
    val fileName: String = "",
    val downloadUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripFilesScreen(
    tripId: String,
    onBack: () -> Unit
) {
    val db = Firebase.firestore
    val context = LocalContext.current
    var files by remember { mutableStateOf<List<TripFile>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // For rename dialog
    var showRenameDialog by remember { mutableStateOf(false) }
    var selectedFileForRename by remember { mutableStateOf<TripFile?>(null) }
    var newFileName by remember { mutableStateOf(TextFieldValue("")) }

    // For delete confirmation
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedFileForDelete by remember { mutableStateOf<TripFile?>(null) }

    fun loadFiles() {
        db.collection("tripDocuments")
            .whereEqualTo("tripId", tripId)
            .get()
            .addOnSuccessListener { result ->
                files = result.map { doc ->
                    TripFile(
                        documentId = doc.id,
                        fileName = doc.getString("fileName") ?: "",
                        downloadUrl = doc.getString("downloadUrl") ?: ""
                    )
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    LaunchedEffect(tripId) {
        loadFiles()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trip Files") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (files.isEmpty()) {
                Text("No files found.", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn {
                    items(files) { file ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = file.fileName,
                                    fontSize = 18.sp,
                                    modifier = Modifier.clickable {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(file.downloadUrl))
                                        context.startActivity(intent)
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    IconButton(onClick = {
                                        selectedFileForRename = file
                                        newFileName = TextFieldValue(file.fileName)
                                        showRenameDialog = true
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Rename")
                                    }
                                    IconButton(onClick = {
                                        selectedFileForDelete = file
                                        showDeleteDialog = true
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Rename Dialog
    if (showRenameDialog && selectedFileForRename != null) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename File") },
            text = {
                TextField(
                    value = newFileName,
                    onValueChange = { newFileName = it },
                    label = { Text("New Name") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val fileToUpdate = selectedFileForRename!!
                    db.collection("tripDocuments").document(fileToUpdate.documentId)
                        .update("fileName", newFileName.text)
                        .addOnSuccessListener {
                            Toast.makeText(context, "File renamed", Toast.LENGTH_SHORT).show()
                            showRenameDialog = false
                            loadFiles()
                        }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && selectedFileForDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete File") },
            text = { Text("Are you sure you want to delete this file?") },
            confirmButton = {
                TextButton(onClick = {
                    val fileToDelete = selectedFileForDelete!!
                    db.collection("tripDocuments").document(fileToDelete.documentId)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(context, "File deleted", Toast.LENGTH_SHORT).show()
                            showDeleteDialog = false
                            loadFiles()
                        }
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
