package com.example.wanderly

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import java.util.*

@Composable
fun TripMapScreen(destination: String, onBack: () -> Unit) {
    val context = LocalContext.current

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                MapView(it).apply {
                    onCreate(null)
                    onResume()
                    getMapAsync { map ->
                        val location = getLocationFromAddress(context, destination)
                        if (location != null) {
                            val latLng = LatLng(location.latitude, location.longitude)
                            map.addMarker(MarkerOptions().position(latLng).title(destination))
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )


        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
    }
}

private fun getLocationFromAddress(context: Context, strAddress: String): Address? {
    return try {
        val coder = Geocoder(context, Locale.getDefault())
        val addresses = coder.getFromLocationName(strAddress, 1)
        if (!addresses.isNullOrEmpty()) addresses[0] else null
    } catch (e: Exception) {
        null
    }
}
