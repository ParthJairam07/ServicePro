package com.example.b07proj   // <-- match the directory

import android.content.Context
import com.example.b07proj.model.DirectLinksModel
import com.example.b07proj.view.Resource
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import com.example.b07proj.presenter.DirectLinksPresenter
import org.mockito.kotlin.verify
import org.mockito.kotlin.never
import kotlin.test.assertTrue

class DirectLinksPresenterBasicTest {

    // ---- mocks ----
    @Mock lateinit var mockModel: DirectLinksModel
    @Mock lateinit var mockContext: Context

    private lateinit var presenter: DirectLinksPresenter  // DirectLinksPresenter is in the same package

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        presenter = DirectLinksPresenter(mockModel)   // inject the mock
    }

    @Test
    fun happyPath_updatesFlowsCorrectly() = runBlocking {
        // 1. stub model calls
        val city = "Toronto"
        val fakeResources = listOf(
            Resource("Victim Services", "Hotline", "416-808-7066", "https://example.com")
        )
        whenever(mockModel.loadWarmupCity()).thenReturn(city)
        whenever(mockModel.loadResources(any())).thenReturn(fakeResources)

        // 2. run the suspend function
        presenter.fetchCityAndResources(mockContext)

        // 3. assert StateFlow values
        assertFalse(presenter.loading.value)          // no longer loading
        assertEquals(city, presenter.warmupCity.value)
        assertEquals(fakeResources, presenter.resources.value)
        assertNull(presenter.errorMessage.value)      // no error
    }

    @Test
    fun nullCity_setsErrorAndSkipsResourceLoad() {
        runBlocking {
            // Arrange
            whenever(mockModel.loadWarmupCity()).thenReturn(null)

            // Act
            presenter.fetchCityAndResources(mockContext)

            // Assert
            assertFalse(presenter.loading.value)
            assertEquals("Failed to load city", presenter.errorMessage.value)
            assertTrue(presenter.resources.value.isEmpty())

            // Verify we never tried to load resources
            verify(mockModel, never()).loadResources(any())
        }
    }

    // ---------------------------------------------------------------------
// 3) loadResources() returns empty list  →  city-specific error message
// ---------------------------------------------------------------------
    @Test
    fun emptyResources_setsCitySpecificError() {
        runBlocking {
            // Arrange
            val city = "Toronto"
            whenever(mockModel.loadWarmupCity()).thenReturn(city)
            whenever(mockModel.loadResources(any())).thenReturn(emptyList())

            // Act
            presenter.fetchCityAndResources(mockContext)

            // Assert
            assertFalse(presenter.loading.value)
            assertEquals("List of resources for $city is empty",
                presenter.errorMessage.value)
            assertTrue(presenter.resources.value.isEmpty())
        }
    }
}
