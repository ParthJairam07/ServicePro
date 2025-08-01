package com.example.b07proj.presenter.contacts

import com.example.b07proj.model.EmergencyContact
// a contract between presenter and view for when viewing contacts
interface ViewContactsContract {
    // presenter -> view

    interface View {
        fun showLoading()
        fun hideLoading()
        fun displayContacts(fetchedContacts: List<EmergencyContact>)
        fun displayEmptyState() // when there is no contacts
        fun displayError(message: String)
        // when we delete a contact, tell view to update its list of contacts (remove the deleted one)
        fun onContactDeleted(contactId: String)

    }
    // view -> presenter
    interface Presenter {
        fun loadContacts()
        // tell presenter to delete a certain contact based on contactId
        fun deleteContact(contactId: String)
        fun onViewDestroyed()
    }

}