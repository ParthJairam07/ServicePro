package com.example.b07proj.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * A singleton object to manage Firebase Authentication and the current user session.
 */
object HandleAuth {

    private val auth: FirebaseAuth = Firebase.auth

    /**
     * Holds the UUID of the currently logged-in user.
     * It is null if no one is logged in.
     * 'private set' means only HandleAuth can change it, but anyone can read it.
     */
    var currentUserUuid: String? = null
        private set

    // When this object is first created, check if a user is already signed in from a previous session.
    init {
        currentUserUuid = auth.currentUser?.uid
    }

    fun signUp(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // On success, update the global UUID
                    currentUserUuid = auth.currentUser?.uid
                    onSuccess()
                } else {
                    onFailure(task.exception?.message ?: "Sign-up failed")
                }
            }
    }

    fun loginWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // On success, update the global UUID
                    currentUserUuid = auth.currentUser?.uid
                    onSuccess()
                } else {
                    onFailure(task.exception?.message ?: "Login failed")
                }
            }
    }

    /**
     * A function for your PIN login flow.
     * You call this after the PIN has been successfully verified.
     */
    fun loginWithPin(uuid: String) {
        // Here, we trust the PIN check was successful and we simply set the user session.
        currentUserUuid = uuid
        Log.d("HandleAuth", "User session started via PIN for UUID: $currentUserUuid")
    }

    fun signInWithGoogle(credential: AuthCredential, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // On success, update the global UUID
                    currentUserUuid = auth.currentUser?.uid
                    onSuccess()
                } else {
                    onFailure(task.exception?.message ?: "Google Sign-In failed")
                }
            }
    }
}