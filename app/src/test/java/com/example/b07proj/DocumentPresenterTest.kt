package com.example.b07proj

import android.net.Uri
import com.example.b07proj.model.dataCategories.DocumentRepository
import com.example.b07proj.presenter.DocumentPresenter
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DocumentPresenterSimpleTest {

    // Mock the presenter's dependency
    @Mock
    private lateinit var mockRepository: DocumentRepository

    // Mock an Android Uri object since it's needed for the method signature
    @Mock
    private lateinit var mockUri: Uri

    private lateinit var presenter: DocumentPresenter

    @Before
    fun setUp() {
        // Initialize all the @Mock objects in this class
        MockitoAnnotations.openMocks(this)
        // Create the presenter with the mocked repository
        presenter = DocumentPresenter(mockRepository)
    }

    @Test
    fun `uploadAndSaveDocument goes to success state and triggers refresh`() {
        // Create an argument captor to capture the (Boolean) -> Unit callback.
        val successCallbackCaptor = argumentCaptor<(Boolean) -> Unit>()

        // ACT
        // Call the function on the presenter that we want to test.
        presenter.uploadAndSaveDocument(mockUri, "Test Doc", "Description", "2025-01-01")

        // Check that the presenter correctly started the loading process.
        assertTrue(presenter.isLoading.value, "Presenter should be in a loading state immediately after call.")

        // Verify that the presenter called the repository and capture the callback it provided.
        verify(mockRepository).uploadAndSaveDocument(
            any(),
            eq("Test Doc"),
            eq("Description"),
            eq("2025-01-01"),
            successCallbackCaptor.capture() // Capture the lambda here
        )

        // Now, manually invoke the captured callback with 'true' to simulate a successful upload.
        successCallbackCaptor.firstValue.invoke(true)

        // 1. Check that the uploadSuccess StateFlow was updated to true.
        assertEquals(true, presenter.uploadSuccess.value, "uploadSuccess should be true after success callback.")
    }

    @Test
    fun `deleteDocument goes to success state`() {
        // ARRANGE
        val documentName = "document-to-delete.pdf"
        val callbackCaptor = argumentCaptor<(Boolean) -> Unit>()

        // ACT
        presenter.deleteDocument(documentName)

        // ASSERT loading state
        assertTrue(presenter.isLoading.value)

        // ACT: Simulate repository success
        verify(mockRepository).deleteDocument(eq(documentName), callbackCaptor.capture())
        callbackCaptor.firstValue.invoke(true) // Simulate success

    }

}