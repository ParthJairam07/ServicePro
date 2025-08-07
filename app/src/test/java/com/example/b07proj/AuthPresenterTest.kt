package com.example.b07proj.presenter

import com.example.b07proj.model.IAuthService
import com.example.b07proj.view.SignUpView
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

class AuthPresenterSimpleTest {

    @Mock
    private lateinit var mockView: SignUpView

    @Mock
    private lateinit var mockAuthService: IAuthService // Mock the INTERFACE

    private lateinit var presenter: AuthPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        // Create the presenter, injecting our mock service
        presenter = AuthPresenter(mockView, mockAuthService)
    }

    @Test
    fun `onSignUpClick when successful, calls view's onSignUpSuccess`() {
        // ARRANGE
        val testEmail = "test@example.com"
        val testPassword = "password123"

        // Tell our mock service to simulate a successful call
        doAnswer { invocation ->
            val onSuccessCallback = invocation.getArgument<() -> Unit>(2)
            onSuccessCallback() // Call the success lambda
            null
        }.whenever(mockAuthService).signUp(any(), any(), any(), any())

        // call the presenter with the test data
        presenter.onSignUpClick(testEmail, testPassword)

        // Verify the view's success method was called.
        verify(mockView).onSignUpSuccess()
        // Verify that no error was shown.
        verify(mockView, never()).showError(any())
    }

    @Test
    fun `onSignUpClick when auth service fails, calls view's showError`() {
        // make a email already in use
        val errorMessage = "Email is already in use"

        // Tell our mock service to simulate a failed call
        doAnswer { invocation ->
            val onFailureCallback = invocation.getArgument<(String) -> Unit>(3)
            onFailureCallback(errorMessage) // Call the failure lambda with an error message
            null
        }.whenever(mockAuthService).signUp(any(), any(), any(), any())

        // now call the presenter
        presenter.onSignUpClick("test@example.com", "password123")


        // Verify the view showed the exact error message from our service.
        verify(mockView).showError(errorMessage)
        // Verify the success method was not called.
        verify(mockView, never()).onSignUpSuccess()
    }
    @Test
    fun `onLoginClick when successful, calls view's onSignUpSuccess`() {
        // make a email already in use
        val testEmail = "test@example.com"
        val testPassword = "password123"

        // Target the loginWithEmail method on our mock service
        doAnswer {
            val onSuccess = it.getArgument<() -> Unit>(2)
            onSuccess() // Simulate success
            null
        }.whenever(mockAuthService).loginWithEmail(any(), any(), any(), any())

        // ACT
        presenter.onLoginClick(testEmail, testPassword)

        // ASSERT
        verify(mockView).onSignUpSuccess()
        verify(mockView, never()).showError(any())
    }
    @Test
    fun `onLoginClick when auth service fails, calls view's showError`() {
        // make a email not found, with wrong password
        val errorMessage = "Wrong password or user not found"
        // Target the loginWithEmail method on our mock service
        // this just simulates a failure
        doAnswer {
            val onFailure = it.getArgument<(String) -> Unit>(3)
            onFailure(errorMessage) // Simulate failure
            null
        }.whenever(mockAuthService).loginWithEmail(any(), any(), any(), any())

        // call the presenter
        presenter.onLoginClick("test@example.com", "wrong-password")

        // make sure its error
        verify(mockView).showError(errorMessage)
        verify(mockView, never()).onSignUpSuccess()
    }
}