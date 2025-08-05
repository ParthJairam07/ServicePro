package com.example.b07proj.presenter

import android.content.Context
import com.example.b07proj.model.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QuizPresenterTestSimple {

    // Mocks for dependencies
    @Mock
    private lateinit var mockRepository: QuizSaveData

    @Mock
    private lateinit var mockContext: Context

    // The class we are testing
    private lateinit var presenter: QuizPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        // Inject the mock repository
        presenter = QuizPresenter(mockRepository)
    }

    // This test does not use a callback, so it remains unchanged.
    @Test
    fun `getQuizData should call repository and return its structured data`() {
        // make the fake data
        val fakeQuestions = Questions(
            Warmup = mapOf("q1" to Question(1, "Q1?", null, "type", "var")),
            BranchQuestions = emptyMap(),
            FollowUp = emptyMap()
        )
        val fakeTips = Tips(questionATips = mapOf("t1" to Tip(id = 1, tip = "Tip1")), emptyMap(), emptyMap())
        val fakeQuizData = QuizData(questions = fakeQuestions, tips = fakeTips)
        // make the mock repository return the fake data
        whenever(mockRepository.getQuizData(mockContext)).thenReturn(fakeQuizData)
        // make the mock context
        val result = presenter.getQuizData(mockContext)

        // verify that the mock repository was called and returned the correct data
        verify(mockRepository).getQuizData(mockContext)
        assertEquals(fakeQuizData, result)
    }

    @Test
    fun `saveResponses when repository succeeds, onComplete is called with true`() {
        // make the fake data
        val testResponses = mapOf("q1" to "answer1")
        val testCollection = "user_responses"
        var onCompleteResult: Boolean? = null // Variable to check the result

        // Tell the mock repository what to do when saveQuizResponses is called.
        // We use `doAnswer` to get access to the arguments passed to the method.
        doAnswer { invocation ->
            // The callback is the 3rd argument (index 2)
            val callback = invocation.getArgument< (Boolean) -> Unit >(2)
            // Invoke the callback with `true` to simulate success
            callback(true)
            null // `doAnswer` requires a return value
        }.whenever(mockRepository).saveQuizResponses(any(), any(), any())


        // Call the presenter's method. This will trigger our `doAnswer` block above.
        presenter.saveResponses(testResponses, testCollection) { success ->
            onCompleteResult = success
        }

        // Check that our onComplete callback was called correctly.
        assertTrue(onCompleteResult == true, "onComplete should have been called with true.")

        //verify that the method was called with the correct arguments.
        verify(mockRepository).saveQuizResponses(eq(testResponses), eq(testCollection), any())
    }

    @Test
    fun `saveResponses when repository fails, onComplete is called with false`() {
        // make the same setup as the previous test
        val testResponses = mapOf("q1" to "answer1")
        val testCollection = "user_responses"
        var onCompleteResult: Boolean? = null

        // Use `doAnswer` again, but this time we simulate a failure.
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean) -> Unit>(2)
            // Invoke the callback with `false` to simulate failure
            callback(false)
            null
        }.whenever(mockRepository).saveQuizResponses(any(), any(), any())


        // mock the save responses method
        presenter.saveResponses(testResponses, testCollection) { success ->
            onCompleteResult = success
        }

        // check that our onComplete callback was called correctly.
        assertFalse(onCompleteResult == true, "onComplete should have been called with false.")
        verify(mockRepository).saveQuizResponses(eq(testResponses), eq(testCollection), any())
    }
}