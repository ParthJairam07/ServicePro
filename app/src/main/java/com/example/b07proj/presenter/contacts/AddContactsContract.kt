package com.example.b07proj.presenter.contacts

// a contract for how the presenter and view talk to each other
interface AddContactsContract {

    // presenter -> view contract
    interface View {
        // basic functions for view to use based on what presenter told view
        fun showLoading()
        fun hideLoading()
        fun showSuccess(message: String)
        fun showError(message: String)
        fun navigateBack()
        // used for editing the contact, we want the answers to be pre-filled in the boxes
        fun displayContactDetails(contactData: Map<String, String>)

    }

    // view -> presenter contract
    interface Presenter {
        // function to handle both adding or editing depending on if contactId is null or not respectfully
        fun saveContact(contactData: Map<String, String>, contactId: String?)
        // getting contact details based on a specific contact id
        fun loadContactDetails(contactId: String)
        fun onViewDestroyed()
    }
}