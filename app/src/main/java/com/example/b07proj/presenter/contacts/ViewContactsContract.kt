package com.example.b07proj.presenter.contacts

import com.example.b07proj.model.EmergencyContact

interface ViewContactsContract {
    // presenter -> view

    interface View {
        fun showLoading()
        fun hideLoading()
        fun displayContacts(fetchedContacts: List<EmergencyContact>)
        fun displayEmptyState() // when there is no contacts
        fun displayError(message: String)
    }
    // view -> presenter
    interface Presenter {
        fun loadContacts()
        fun onViewDestroyed()
    }

}