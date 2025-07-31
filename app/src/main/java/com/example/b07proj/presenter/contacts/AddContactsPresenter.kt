package com.example.b07proj.presenter.contacts

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddContactsPresenter( var view: AddContactsContract.View?) : AddContactsContract.Presenter {

    private val responses = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun addContact(contactData: Map<String, String>) {
        // adding a contact so we are loading now
        view?.showLoading()

        val user = auth.currentUser

        if (user == null) {
            view?.showError("Error: Authentication Failed")
            view?.hideLoading()
            return
        }
        // now store information in firestore
        val contactsData = responses.collection("users")
            .document(user.uid)
            .collection("emergency_contacts")

        contactsData.add(contactData)
            .addOnSuccessListener {
                view?.hideLoading()
                view?.showSuccess("Contact added successfully!")
                view?.navigateBack()

            }
            .addOnFailureListener { exception ->
                view?.hideLoading()
                view?.showError("Failed to add contact ${exception.message}")
            }
    }
    override fun onViewDestroyed() {
        view = null
    }

}