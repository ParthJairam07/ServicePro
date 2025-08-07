package com.example.b07proj.presenter.dataItems

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
// we implement the contract for view -> presenter
class AddDataItemPresenter(var view: AddDataItemContract.View?) : AddDataItemContract.Presenter {

    // the data base
    private val responses = FirebaseFirestore.getInstance()
    //  for checking the current authentication state
    private val auth = FirebaseAuth.getInstance()

    // does either adding or editing
    override fun saveDataItem(category: Categories, itemData: Map<String, String>, dataItemId: String?) {
        // saving a contact so we are loading now
        view?.showLoading()

        // check if the user is actually authenticated
        val user = auth.currentUser
        if (user == null) {
            view?.showError("Error: Authentication Failed")
            view?.hideLoading()
            return
        }

        // get user emergency contact list
        val categoryCollection = categoryCollectionMap[category]

        if (categoryCollection == null) {
            view?.showError("Error: Invalid Category")
            return;
        }

        // now update/store information in firestore
        // contactsData has all contacts in subcollection emergency_contacts
        val contactsData = responses.collection("users")
            .document(user.uid)
            .collection(categoryCollection)

        // check if our task is either editing or adding
        val task = if (dataItemId != null) {
            // if we have a contactId -> in edit mode
            // just update existing contact id with new contact data
            contactsData.document(dataItemId).update(itemData)
        }
        else {
            // in add mode
            contactsData.add(itemData)
        }
        task.addOnSuccessListener {
            // we done so stop loading
            view?.hideLoading()
            val message = if (dataItemId != null) {
                "Updated Info!"
            }
            else {
                "Added Info!"
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

    override fun loadDataItemDetails(category: Categories, dataItemId: String) {
        // check authentication of user
        val user = auth.currentUser
        if (user == null) {
            view?.showError("Error: User not found")
            return
        }

        // get user emergency contact list
        val categoryCollection = categoryCollectionMap[category]

        if (categoryCollection == null) {
            view?.showError("Error: Authentication Failed")
            return;
        }

        // get document of contact with corresponding contactId
        responses.collection("users").document(user.uid)
            .collection(categoryCollection)
            .document(dataItemId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // firestore gives data as Map<String,Any> so convert to String, String
                    val contactData = documentSnapshot.data?.mapValues { it.value.toString() }
                    if (contactData != null) {
                        // we tell view to prefill the information into the boxes
                        view?.displayDataItemDetails(contactData)
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