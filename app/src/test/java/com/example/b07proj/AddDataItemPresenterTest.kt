package com.example.b07proj

import com.example.b07proj.presenter.dataItems.AddDataItemContract
import com.example.b07proj.presenter.dataItems.AddDataItemPresenter
import com.example.b07proj.presenter.dataItems.Categories
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AddDataItemPresenterTest {
    @Mock private lateinit var mockView: AddDataItemContract.View
    // presenter directly calls getInstance() but these call fail since not initialized so we need mock objects
    @Mock private lateinit var mockAuth: FirebaseAuth
    @Mock private lateinit var mockDataBase: FirebaseFirestore
    @Mock private lateinit var mockUser: FirebaseUser
    @Mock private lateinit var mockCollectionReference: CollectionReference
    @Mock private lateinit var mockDocumentReference: DocumentReference
    // db operations return Task objects and snapshots
    @Mock private lateinit var mockTask: Task<Void>
    @Mock private lateinit var mockGetTask: Task<DocumentSnapshot>
    @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
    @Mock private lateinit var mockAddTask: Task<DocumentReference>


    private lateinit var mockedAuth: MockedStatic<FirebaseAuth>
    private lateinit var mockedFirestore: MockedStatic<FirebaseFirestore>

    private lateinit var presenter: AddDataItemPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        mockedAuth = Mockito.mockStatic(FirebaseAuth::class.java)
        mockedFirestore = Mockito.mockStatic(FirebaseFirestore::class.java)

        // everytime we we see getInstance() from auth or Firestore, we use our mock objects instead
        whenever(FirebaseAuth.getInstance()).thenReturn(mockAuth)
        whenever(FirebaseFirestore.getInstance()).thenReturn(mockDataBase)


        presenter = AddDataItemPresenter(mockView)

        // everytime we want to get current user, uid or any collection/document we use mock object instead
        whenever(mockAuth.currentUser).thenReturn(mockUser)
        // abinash@gmail.com UID
        whenever(mockUser.uid).thenReturn("peaHRPbtcaUJ0KEa9COLvj0tx5m2")
        whenever(mockDataBase.collection(any())).thenReturn(mockCollectionReference)
        whenever(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
        whenever(mockDocumentReference.collection(any())).thenReturn(mockCollectionReference)
    }
    @After
    fun closeMocks() {
        mockedAuth.close()
        mockedFirestore.close()
    }

    @Test
    fun `saveDataItem when user is not logged in, show the error and stop`() {
        // whenever our we see MockAuth.current user in saveDataItem, return null instead
        whenever(mockAuth.currentUser).thenReturn(null)
        presenter.saveDataItem(Categories.EMERGENCY_CONTACTS, emptyMap(), null)

        verify(mockView).showLoading()
        verify(mockView).showError("Error: Authentication Failed")
        verify(mockView).hideLoading()
        // verify that did not change anything in the database
        verify(mockDataBase, never()).collection(any())

    }
    @Test
    fun `SaveDataItem when user adds new item, shows suceed toast and navigate back`() {
        mockAddTask.mockSuccess(mockDocumentReference)

        whenever(mockCollectionReference.add(any<Map<String, Any>>()))
            .thenReturn(mockAddTask)

        // a contact to add
        val itemData = mapOf("contactName" to "Abinash", "contactPhoneNumber" to "111-222-3333",
            "contactRelation" to "Partner", "contactEmail" to "abinash123@gmail.com")
        // add itemData contact
        presenter.saveDataItem(Categories.EMERGENCY_CONTACTS, itemData, null)

        verify(mockView).showLoading()
        verify(mockCollectionReference).add(itemData)
        verify(mockView).hideLoading()
        verify(mockView).showSuccess("Added Info!")
        verify(mockView).navigateBack()
        verify(mockView, never()).showError(any())
    }

    @Test
    fun `saveDataItem updating an existing item succeeds shows success and navigates back`() {

        mockTask.mockSuccess(null)

        whenever(mockDocumentReference.update(any<Map<String, Any>>()))
            .thenReturn(mockTask)

        val itemData = mapOf("contactName" to "Abinash", "contactPhoneNumber" to "111-222-3333",
            "contactRelation" to "Partner", "contactEmail" to "abinash123@gmail.com")
        presenter.saveDataItem(Categories.EMERGENCY_CONTACTS, itemData, "S8PVAk1kydhQ4qPR6rEN")

        verify(mockView).showLoading()
        // make sure we got the same document
        verify(mockCollectionReference).document("S8PVAk1kydhQ4qPR6rEN")
        verify(mockDocumentReference).update(itemData)
        verify(mockView).hideLoading()
        verify(mockView).showSuccess("Updated Info!")
        verify(mockView).navigateBack()
        verify(mockView, never()).showError(any())
    }

    @Test
    fun `saveDataItem when firestore error shows error message`() {
        val mockException = Exception("Firestore error")
        mockAddTask.mockFailure(mockException)

        whenever(mockCollectionReference.add(any<Map<String, Any>>()))
            .thenReturn(mockAddTask)

        presenter.saveDataItem(Categories.SAFE_LOCATIONS, emptyMap(), null)

        // Assert
        verify(mockView).showLoading()
        verify(mockView).hideLoading()
        verify(mockView).showError("Error saving item: Firestore error")
        verify(mockView, never()).showSuccess(any())
    }

    @Test
    fun `saveDataItem editing an existing safe location, succeeds shows success`() {

        mockTask.mockSuccess(null)
        whenever(mockDocumentReference.update(any<Map<String, Any>>()))
            .thenReturn(mockTask)

        val itemData = mapOf("safeLocationAddress" to "Homev2",
            "safeLocationDescription" to "super safe", "safeLocationName" to "my home")
        presenter.saveDataItem(Categories.SAFE_LOCATIONS, itemData, "oqweO3dRTddEGHJWh1ZR")

        verify(mockView).showLoading()
        verify(mockDocumentReference).update(itemData)
        verify(mockView).hideLoading()
        verify(mockView).showSuccess("Updated Info!")
        verify(mockView).navigateBack()
    }
    @Test
    fun `loadDataItemDetails successful displays details`() {

        val itemData = mapOf("safeLocationAddress" to "Homev2",
            "safeLocationDescription" to "super safe", "safeLocationName" to "my home")

        // we use itemData instead of actual user data and fake that it exist
        whenever(mockDocumentSnapshot.exists()).thenReturn(true)
        whenever(mockDocumentSnapshot.data).thenReturn(itemData)

        mockGetTask.mockSuccess(mockDocumentSnapshot)
        whenever(mockDocumentReference.get()).thenReturn(mockGetTask)

        presenter.loadDataItemDetails(Categories.SAFE_LOCATIONS, "oqweO3dRTddEGHJWh1ZR")

        verify(mockView).displayDataItemDetails(itemData)
        verify(mockView, never()).showError(any())
    }
}

// mock addOnSuccessListener
private fun <T> Task<T>.mockSuccess(result: T?): Task<T> {

    whenever(this.isSuccessful).thenReturn(true)

    whenever(this.result).thenReturn(result)

    whenever(this.exception).thenReturn(null)
    // Intercept  addOnSuccessListener call
    whenever(this.addOnSuccessListener(any())).thenAnswer {
        val listener = it.arguments[0] as OnSuccessListener<T?>
        // pass in our mock result object instead for onSuccess()
        listener.onSuccess(result)
        return@thenAnswer this
    }
    // dont do anything for when addOnFailureListener
    whenever(this.addOnFailureListener(any())).thenReturn(this)
    return this
}

private fun <T> Task<T>.mockFailure(exception: Exception): Task<T> {
    whenever(this.isSuccessful).thenReturn(false)
    whenever(this.result).thenReturn(null)
    whenever(this.exception).thenReturn(exception)
    whenever(this.addOnSuccessListener(any())).thenReturn(this)

    whenever(this.addOnFailureListener(any())).thenAnswer {
        // get listener code that presenter created
        val listener = it.arguments[0] as OnFailureListener
        listener.onFailure(exception)
        return@thenAnswer this
    }
    return this
}