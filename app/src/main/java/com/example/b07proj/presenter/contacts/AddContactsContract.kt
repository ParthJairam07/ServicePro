package com.example.b07proj.presenter.contacts

interface AddContactsContract {

    // presenter -> view contract
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showSuccess(message: String)
        fun showError(message: String)
        fun navigateBack()
    }

    // view -> presenter contract
    interface Presenter {
        fun addContact(contactData: Map<String, String>)
        fun onViewDestroyed()
    }
}