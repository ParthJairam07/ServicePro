package com.example.b07proj.presenter.dataItems

// a contract between presenter and view for when viewing contacts
interface ViewDataItemContract {
    // presenter -> view

    interface View <T> {
        fun showLoading()
        fun hideLoading()
        fun displayContacts(fetchedContacts: List<T>)
        fun displayEmptyState() // when there is no contacts
        fun displayError(message: String)
        // when we delete a contact, tell view to update its list of contacts (remove the deleted one)
        fun onContactDeleted(contactId: String)

    }
    // view -> presenter
    interface Presenter<T> {

        fun loadContacts(category: Categories, itemClass: Class<T>)
        // tell presenter to delete a certain contact based on contactId
        fun deleteContact(categories: Categories, contactId: String)
        fun onViewDestroyed()
    }

}