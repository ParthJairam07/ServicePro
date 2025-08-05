package com.example.b07proj.model

import com.google.firebase.auth.AuthCredential

// An interface describing the contract for any authentication service
interface IAuthService {
    fun signUp(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    )

    fun loginWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    )

    fun signInWithGoogle(
        credential: AuthCredential,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    )
}