package com.example.b07proj.presenter.contacts


import com.example.b07proj.model.EmergencyContact
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
class ViewContactsPresenter(var view: ViewContactsContract.View?) : ViewContactsContract.Presenter {

    private val contacts = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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
                view?.hideLoading()
                view?.displayError("Error getting contacts: ${exception.message}")
            }
    }
    override fun onViewDestroyed() {
        view = null
    }

}