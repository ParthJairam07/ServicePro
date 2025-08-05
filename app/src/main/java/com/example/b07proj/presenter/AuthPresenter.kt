package com.example.b07proj.presenter

import com.example.b07proj.model.HandleAuth
import com.example.b07proj.model.IAuthService
import com.example.b07proj.view.SignUpView
import com.google.firebase.auth.GoogleAuthProvider

class AuthPresenter (
    public val view: SignUpView,
    private val authService: IAuthService = HandleAuth
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
        authService.signUp(email, password,
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
        authService.loginWithEmail(email, password,
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
        authService.signInWithGoogle(credential,
            onSuccess = {
                view.onSignUpSuccess()
            },
            onFailure = { errorMessage ->
                view.showError(errorMessage)
            }
        )
    }

    private fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) {
            return false
        }
        // A common and simple regex for email validation.
        val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
        return emailRegex.matches(email)
    }
}