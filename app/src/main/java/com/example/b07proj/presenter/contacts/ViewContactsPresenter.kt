package com.example.b07proj.presenter.contacts


import com.example.b07proj.model.EmergencyContact
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
// implementing the contract for view -> presenter
class ViewContactsPresenter(var view: ViewContactsContract.View?) : ViewContactsContract.Presenter {

    // used for getting all contacts from data base
    private val contacts = FirebaseFirestore.getInstance()
    // for authentication of user
    private val auth = FirebaseAuth.getInstance()
    // the user uid
    private val userUid : String? get() = auth.currentUser?.uid

    override fun loadContacts() {
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
        contacts.collection("users")
            .document(user.uid)
            .collection("emergency_contacts")
            .get()
            .addOnSuccessListener { querySnapshot  ->
                view?.hideLoading()
                if (querySnapshot.isEmpty) {
                    // we have a separate display for empty display
                    view?.displayEmptyState()
                }
                else {
                    // let firestore convert each document into a EmergencyContact object
                    val contacts = querySnapshot.toObjects(EmergencyContact::class.java)
                    // call view to display the contacts now
                    view?.displayContacts(contacts)

                }
            }
            .addOnFailureListener { exception ->
                // failure means we finish loading and display an error
                view?.hideLoading()
                view?.displayError("Error getting contacts: ${exception.message}")
            }
    }
    // delete contact based on contactId
    override fun deleteContact(contactId: String) {
        // auth checking
        val currentUserId = userUid
        if (currentUserId == null) {
            view?.displayError("Failed to get current user id")
            return
        }
        // if we didnt get a proper id display error
        if (contactId.isEmpty()) {
            view?.displayError("Invalid id for item")
            return
        }
        // get specific document to delete
        contacts.collection("users").document(currentUserId)
            .collection("emergency_contacts").document(contactId)
            .delete()
            .addOnSuccessListener {
                // tell view that the contact is deleted
                view?.onContactDeleted(contactId)
            }
            .addOnFailureListener { exception ->
                view?.displayError("Error deleting contact: ${exception.message}")
            }

    }
    override fun onViewDestroyed() {
        view = null
    }

}