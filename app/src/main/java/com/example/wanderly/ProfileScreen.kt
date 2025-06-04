package com.example.wanderly

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import java.util.*

@Composable
fun ProfileScreen(onBackToMenu: () -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current

    var photoUrl by remember { mutableStateOf(user?.photoUrl?.toString()) }
    val name = user?.displayName ?: "User"
    val email = user?.email ?: "No email"
    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            isUploading = true
            val file = uriToFile(context, uri)
            CloudinaryUploader.uploadFile(file) { success, url ->
                if (success && url != null) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(Uri.parse(url))
                        .build()

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            photoUrl = url
                            Log.d("ProfileImage", "Uploaded photo URL: $photoUrl")

                            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                        }
                        isUploading = false
                    }
                } else {
                    Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
                    isUploading = false
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB3E5FC))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (photoUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(photoUrl),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .clickable { launcher.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.profile_icon),
                    contentDescription = "Default Profile Picture",
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .clickable { launcher.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = name, fontSize = 26.sp, color = Color.Black)
            Text(text = email, fontSize = 16.sp, color = Color.DarkGray)

            if (isUploading) {
                Spacer(modifier = Modifier.height(12.dp))
                CircularProgressIndicator()
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onBackToMenu,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Back to Menu")
            }
        }
    }
}
