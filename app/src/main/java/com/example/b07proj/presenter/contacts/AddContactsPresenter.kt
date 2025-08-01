package com.example.b07proj.presenter.contacts

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
// we implement the contract for view -> presenter
class AddContactsPresenter( var view: AddContactsContract.View?) : AddContactsContract.Presenter {

    // the data base
    private val responses = FirebaseFirestore.getInstance()
    //  for checking the current authentication state
    private val auth = FirebaseAuth.getInstance()

    // does either adding or editing
    override fun saveContact(contactData: Map<String, String>, contactId: String?) {
        // saving a contact so we are loading now
        view?.showLoading()

        // check if the user is actually authenticated
        val user = auth.currentUser
        if (user == null) {
            view?.showError("Error: Authentication Failed")
            view?.hideLoading()
            return
        }
        // now update/store information in firestore
        // contactsData has all contacts in subcollection emergency_contacts
        val contactsData = responses.collection("users")
            .document(user.uid)
            .collection("emergency_contacts")

        // check if our task is either editing or adding
        val task = if (contactId != null) {
            // if we have a contactId -> in edit mode
            // just update existing contact id with new contact data
            contactsData.document(contactId).update(contactData)
        }
        else {
            // in add mode
            contactsData.add(contactData)
        }
        task.addOnSuccessListener {
            // we done so stop loading
            view?.hideLoading()
            val message = if (contactId != null) {
                "Contact Updated!"
            }
            else {
                "Contact Added!"
            }
            // tell view to show that we did it and go back to previous screen
            view?.showSuccess(message)
            view?.navigateBack()
        }
            .addOnFailureListener { exception ->
                view?.hideLoading()
                view?.showError("Error saving item: ${exception.message}")
        }
    }

    override fun loadContactDetails(contactId: String) {
        // check authentication of user
        val user = auth.currentUser
        if (user == null) {
            return
        }
        // get document of contact with corresponding contactId
        responses.collection("users").document(user.uid)
            .collection("emergency_contacts")
            .document(contactId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // firestore gives data as Map<String,Any> so convert to String, String
                    val contactData = documentSnapshot.data?.mapValues { it.value.toString() }
                    if (contactData != null) {
                        // we tell view to prefill the information into the boxes
                        view?.displayContactDetails(contactData)
                    }
                }
                else {
                    view?.showError("Data item not found")
                }
            }
            .addOnFailureListener {
                view?.showError("Failed to load data item details")
            }
    }
    override fun onViewDestroyed() {
        view = null
    }

}