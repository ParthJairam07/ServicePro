package com.example.b07proj.presenter.dataItems


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

enum class Categories {
    EMERGENCY_CONTACTS,
    MEDICATIONS,
    SAFE_LOCATIONS
}
// Associates categories with collection name
val categoryCollectionMap = mapOf(
    Categories.EMERGENCY_CONTACTS to "emergency_contacts",
    Categories.MEDICATIONS to "medications",
    Categories.SAFE_LOCATIONS to "safe_locations"
)

// implementing the contract for view -> presenter
class ViewContactsPresenter<T>(var view: ViewDataItemContract.View<T>?) : ViewDataItemContract.Presenter<T> {

    // used for getting all contacts from data base
    private val contacts = FirebaseFirestore.getInstance()
    // for authentication of user
    private val auth = FirebaseAuth.getInstance()
    // the user uid
    private val userUid : String? get() = auth.currentUser?.uid

    override fun loadContacts(category: Categories, itemClass: Class<T>) {
        // start showing we are currently loading in contacts
        view?.showLoading()
        // get current user of whoever auth
        val user = auth.currentUser
        // if we couldn't find the current user, display error and hide loading
        if (user == null) {
            view?.hideLoading()
            view?.displayError("Error: Authentication Failed")
            return
        }
        // get user emergency contact list
        val categoryCollection = categoryCollectionMap[category]

        if (categoryCollection == null) {
            view?.displayError("Error getting data item: Invalid category")
            return;
        }

        contacts.collection("users")
            .document(user.uid)
            .collection(categoryCollection)
            .get()
            .addOnSuccessListener { querySnapshot  ->
                view?.hideLoading()
                if (querySnapshot.isEmpty) {
                    // we have a separate display for empty display
                    view?.displayEmptyState()
                }
                else {
                    // let firestore convert each document into a EmergencyContact object
                    val contacts = querySnapshot.toObjects(itemClass)
                    // call view to display the contacts now
                    view?.displayContacts(contacts)

                }
            }
            .addOnFailureListener { exception ->
                // failure means we finish loading and display an error
                view?.hideLoading()
                view?.displayError("Error getting data item: ${exception.message}")
            }
    }
    // delete contact based on contactId
    override fun deleteContact(categories: Categories, contactId: String) {
        // auth checking
        val currentUserId = userUid
        if (currentUserId == null) {
            view?.displayError("Failed to get current user id")
            return
        }
        // if we didn't get a proper id display error
        if (contactId.isEmpty()) {
            view?.displayError("Invalid id for item")
            return
        }
        // get user emergency contact list
        val categoryCollection = categoryCollectionMap[categories]

        if (categoryCollection == null) {
            view?.displayError("Error getting data item: Invalid category")
            return;
        }

        // get specific document to delete
        contacts.collection("users").document(currentUserId)
            .collection(categoryCollection).document(contactId)
            .delete()
            .addOnSuccessListener {
                // tell view that the contact is deleted
                view?.onContactDeleted(contactId)
            }
            .addOnFailureListener { exception ->
                view?.displayError("Error deleting data item: ${exception.message}")
            }

    }
    override fun onViewDestroyed() {
        view = null
    }

}