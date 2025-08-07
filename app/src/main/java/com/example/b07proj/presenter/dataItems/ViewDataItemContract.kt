package com.example.b07proj.presenter.dataItems

// a contract between presenter and view for when viewing data items
interface ViewDataItemContract {
    // presenter -> view

    interface View <T> {
        fun showLoading()
        fun hideLoading()
        fun displayContacts(fetchedContacts: List<T>)
        fun displayEmptyState() // when there is no data items
        fun displayError(message: String)
        // when we delete a data item, tell view to update its list of data items (remove the deleted one)
        fun onDataItemDeleted(dataItemId: String)

    }
    // view -> presenter
    interface Presenter<T> {

        fun loadDataItems(category: Categories, itemClass: Class<T>)
        // tell presenter to delete a certain contact based on contactId
        fun deleteDataItem(categories: Categories, dataItemId: String)
        fun onViewDestroyed()
    }

}