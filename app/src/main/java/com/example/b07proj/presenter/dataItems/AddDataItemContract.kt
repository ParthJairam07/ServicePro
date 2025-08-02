package com.example.b07proj.presenter.dataItems

// a contract for how the presenter and view talk to each other
interface AddDataItemContract {

    // presenter -> view contract
    interface View {
        // basic functions for view to use based on what presenter told view
        fun showLoading()
        fun hideLoading()
        fun showSuccess(message: String)
        fun showError(message: String)
        fun navigateBack()
        // used for editing the data item, we want the answers to be pre-filled in the boxes
        fun displayDataItemDetails(itemData: Map<String, String>)

    }

    // view -> presenter contract
    interface Presenter {
        // function to handle both adding or editing depending on if dataItemId is null or not respectfully
        fun saveDataItem(category: Categories, itemData: Map<String, String>, dataItemId: String?)
        // getting item data details based on a specific data item id
        fun loadDataItemDetails(category: Categories, dataItemId: String)
        fun onViewDestroyed()
    }
}