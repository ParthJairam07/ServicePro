package com.example.b07proj.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// Singleton object that implements the IAuthService interface.
object HandleAuth : IAuthService{

    val auth: FirebaseAuth = Firebase.auth

    // Global UUID for the currently signed-in user.
    var currentUserUuid: String? = null
        //setter for the uuid
        set

    // When this object is first created, check if a user is already signed in from a previous session.
    init {
        currentUserUuid = auth.currentUser?.uid
    }
    // Sign up a new user with email and password.
    override fun signUp(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        // firebase handles this for us
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
    // login a user with email and password
    override fun loginWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        // firebase sign in
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

    // Signin user with google account
    override fun signInWithGoogle(credential: AuthCredential, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
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