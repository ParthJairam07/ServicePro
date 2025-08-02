package com.example.b07proj.presenter

import com.example.b07proj.model.HandleAuth // Make sure you import the object
import com.example.b07proj.view.SignUpView
import com.google.firebase.auth.GoogleAuthProvider

class AuthPresenter (
    val view: SignUpView
){

    fun onSignUpClick(email: String, password: String) {
        if (!isValidEmail(email)) {
            view.showError("Invalid email format")
            return
        }
        if (password.length < 6) {
            view.showError("Password must be at least 6 characters")
            return
        }

        //Call the method directly on the singleton object `HandleAuth`.
        HandleAuth.signUp(email, password,
            onSuccess = {
                view.onSignUpSuccess()
            },
            onFailure = {
                view.showError(it)
            }
        )
    }

    fun onLoginClick(email: String, password: String) {
        if (!isValidEmail(email)) {
            view.showError("Invalid email format")
            return
        }
        if (password.length < 6) {
            view.showError("Password must be at least 6 characters")
            return
        }

        // Call the method directly on the singleton object `HandleAuth`.
        HandleAuth.loginWithEmail(email, password,
            onSuccess = {
                view.onSignUpSuccess()
            },
            onFailure = {
                view.showError(it)
            }
        )
    }

    fun onGoogleSignInSucceeded(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        // Call the method directly on the singleton object `HandleAuth`.
        HandleAuth.signInWithGoogle(credential,
            onSuccess = {
                view.onSignUpSuccess()
            },
            onFailure = { errorMessage ->
                view.showError(errorMessage)
            }
        )
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}