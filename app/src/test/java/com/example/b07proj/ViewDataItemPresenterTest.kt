package com.example.b07proj

import com.example.b07proj.model.dataCategories.EmergencyContact
import com.example.b07proj.model.dataCategories.Medication
import com.example.b07proj.model.dataCategories.SafeLocation
import com.example.b07proj.presenter.dataItems.Categories
import com.example.b07proj.presenter.dataItems.ViewContactsPresenter
import com.example.b07proj.presenter.dataItems.ViewDataItemContract
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
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

class ViewDataItemPresenterTest {

    // 3 views mocks for each data category (except documents)
    @Mock private lateinit var mockViewContact: ViewDataItemContract.View<EmergencyContact>
    @Mock private lateinit var mockViewSafeLocation: ViewDataItemContract.View<SafeLocation>
    @Mock private lateinit var mockViewMedications: ViewDataItemContract.View<Medication>

    @Mock private lateinit var mockAuth: FirebaseAuth
    @Mock private lateinit var mockDatabase: FirebaseFirestore
    @Mock private lateinit var mockUser: FirebaseUser
    @Mock private lateinit var mockCollectionReference: CollectionReference
    @Mock private lateinit var mockDocumentReference: DocumentReference

    // mocks for certain actions/operations
    @Mock private lateinit var mockQuerySnapshot: QuerySnapshot
    // getting item from list
    @Mock private lateinit var mockGetListTask: Task<QuerySnapshot>
    // deleting item off list
    @Mock private lateinit var mockDeleteTask: Task<Void>

    // firebase auth and firestore have static methods like getInstance()
    // we want to replace it with something else
    private lateinit var mockedAuth: MockedStatic<FirebaseAuth>
    private lateinit var mockedFirestore: MockedStatic<FirebaseFirestore>

    private lateinit var presenterContact: ViewContactsPresenter<EmergencyContact>
    private lateinit var presenterSafeLocation: ViewContactsPresenter<SafeLocation>
    private lateinit var presenterMedication: ViewContactsPresenter<Medication>

    @Before
    fun setUp() {
        // initialize all mock objects
        MockitoAnnotations.openMocks(this)
        // static methods needs to be replaced
        mockedAuth = Mockito.mockStatic(FirebaseAuth::class.java)
        mockedFirestore = Mockito.mockStatic(FirebaseFirestore::class.java)

        // replace .getInstance returns with mock objects
        whenever(FirebaseAuth.getInstance()).thenReturn(mockAuth)
        whenever(FirebaseFirestore.getInstance()).thenReturn(mockDatabase)

        presenterContact = ViewContactsPresenter(mockViewContact)
        presenterSafeLocation = ViewContactsPresenter(mockViewSafeLocation)
        presenterMedication = ViewContactsPresenter(mockViewMedications)

        // since we arent connecting to firebase, we need to replace finding user data with mock objects
        whenever(mockAuth.currentUser).thenReturn(mockUser)
        // abinash@gmail.com uid
        whenever(mockUser.uid).thenReturn("peaHRPbtcaUJ0KEa9COLvj0tx5m2")
        whenever(mockDatabase.collection(any())).thenReturn(mockCollectionReference)
        whenever(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
        whenever(mockDocumentReference.collection(any())).thenReturn(mockCollectionReference)
    }
    @After
    fun closeMocks() {
        mockedAuth.close()
        mockedFirestore.close()
    }

    @Test
    fun `loadDataItems when user is not logged in, show the error and stop`() {
        // our currentUser was not found
        whenever(mockAuth.currentUser).thenReturn(null)

        // testing for loading contacts
        presenterContact.loadDataItems(Categories.EMERGENCY_CONTACTS, EmergencyContact::class.java)
        verify(mockViewContact).showLoading()
        verify(mockViewContact).hideLoading()
        verify(mockViewContact).displayError("Error: Authentication Failed")
    }

    @Test
    fun `loadDataItems is successful and list is not empty, display the medications`() {
        // Abinash Actual Medications in real life!
        val fakeMedications = listOf(
            Medication(id = "9009", medicationName = "Tacrolimus otintment", medicationDosage = "0.1%", medicationExpiry = "2025-08-04"),
            Medication(id = "9010", medicationName = "Teva-Mometasone", medicationDosage = "a blob", medicationExpiry = "2025-08-04"),
        )
        // the list isn't empty
        whenever(mockQuerySnapshot.isEmpty).thenReturn(false)
        // the snapshot we get is turned into fakeMedications
        whenever(mockQuerySnapshot.toObjects(Medication::class.java)).thenReturn(fakeMedications)
        // getting the list is a sucess, the result is our snapshot
        mockGetListTask.mockSuccess(mockQuerySnapshot)
        whenever(mockCollectionReference.get()).thenReturn(mockGetListTask)

        presenterMedication.loadDataItems(Categories.MEDICATIONS, Medication::class.java)

        verify(mockViewMedications).showLoading()
        verify(mockViewMedications).hideLoading()
        verify(mockViewMedications).displayContacts(fakeMedications)

    }
    @Test
    fun `loadDataItems when successful and list is emptty, displays empty state`() {
        // empty list is true
        whenever(mockQuerySnapshot.isEmpty).thenReturn(true)


        mockGetListTask.mockSuccess(mockQuerySnapshot)
        whenever(mockCollectionReference.get()).thenReturn(mockGetListTask)

        presenterSafeLocation.loadDataItems(Categories.SAFE_LOCATIONS, SafeLocation::class.java)

        verify(mockViewSafeLocation).showLoading()
        verify(mockViewSafeLocation).hideLoading()
        verify(mockViewSafeLocation).displayEmptyState()
        verify(mockViewSafeLocation, never()).displayContacts(any())
    }
    @Test
    fun `loadDataItems when firestore error, shows error`() {
        val mockException = Exception("Firestore Error")

        mockGetListTask.mockFailure(mockException)
        whenever(mockCollectionReference.get()).thenReturn(mockGetListTask)

        presenterContact.loadDataItems(Categories.EMERGENCY_CONTACTS, EmergencyContact::class.java)
        verify(mockViewContact).showLoading()
        verify(mockViewContact).hideLoading()
        verify(mockViewContact).displayError("Error getting data item: Firestore Error")
    }

    @Test
    fun `deleteDataItem successful, calls onContactDeleted onto view`() {
        mockDeleteTask.mockSuccess(null)
        whenever(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
        whenever(mockDocumentReference.delete()).thenReturn(mockDeleteTask)


        presenterSafeLocation.deleteDataItem(Categories.SAFE_LOCATIONS, "oqweO3dRTddEGHJWh1ZR")

        verify(mockCollectionReference).document("oqweO3dRTddEGHJWh1ZR")
        verify(mockDocumentReference).delete()
        verify(mockViewSafeLocation).onDataItemDeleted("oqweO3dRTddEGHJWh1ZR")
        verify(mockViewSafeLocation, never()).displayError(any())
    }

    @Test
    fun `deleteDataItem when firestore error, shows error`() {
        val mockException = Exception("Firestore Error")

        mockDeleteTask.mockFailure(mockException)
        whenever(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
        whenever(mockDocumentReference.delete()).thenReturn(mockDeleteTask)

        presenterContact.deleteDataItem(Categories.EMERGENCY_CONTACTS, "oqweO3dRTddEGHJWh1ZR")

        verify(mockViewContact).displayError("Error deleting data item: Firestore Error")
        verify(mockViewContact, never()).onDataItemDeleted(any())
    }

    @Test
    fun `deleteDataItem with empty ID shows error and does not call to firestore`() {

        presenterContact.deleteDataItem(Categories.EMERGENCY_CONTACTS, "") // Empty ID

        verify(mockViewContact).displayError("Invalid id for item")
        verify(mockCollectionReference, never()).document(any())
        verify(mockDocumentReference, never()).delete()
    }






}