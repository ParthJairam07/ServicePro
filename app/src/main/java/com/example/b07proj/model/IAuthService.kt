package com.example.b07proj.model

import com.google.firebase.auth.AuthCredential

// An interface describing the contract for any authentication service
interface IAuthService {
    // Function to sign up a new user with email and password
    fun signUp(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    )
    // Function to log in a user with email and password
    fun loginWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    )
    // Function to sign in a user with a Google authentication credential
    fun signInWithGoogle(
        credential: AuthCredential,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    )
}