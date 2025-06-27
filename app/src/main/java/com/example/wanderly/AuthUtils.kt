package com.example.wanderly

import android.util.Log
import com.google.firebase.auth.FirebaseAuth


fun signUpWithEmailAndPassword(
    email: String,
    password: String,
    onResult: (Boolean) -> Unit
) {
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            onResult(task.isSuccessful)
            if (!task.isSuccessful) {
                Log.e("Auth", "Sign-up failed", task.exception)
            }
        }
}

fun signInWithEmailAndPassword(
    email: String,
    password: String,
    onResult: (Boolean) -> Unit
) {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            onResult(task.isSuccessful)
            if (!task.isSuccessful) {
                Log.e("Auth", "Login failed", task.exception)
            }
        }
}
