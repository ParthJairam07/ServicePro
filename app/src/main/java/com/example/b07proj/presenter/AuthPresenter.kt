package com.example.b07proj.presenter

import com.example.b07proj.model.HandleAuth
import com.example.b07proj.view.SignUpView

class AuthPresenter (
    private val auth: HandleAuth,
    private val view: SignUpView
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

        auth.signUp(email, password,
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
        auth.loginWithEmail(email, password,
            onSuccess = {
                view.onSignUpSuccess()
            },
            onFailure = {
                view.showError(it)
            }
        )
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}