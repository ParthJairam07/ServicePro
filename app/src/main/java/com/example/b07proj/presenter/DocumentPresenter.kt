package com.example.b07proj.presenter

import android.net.Uri
import com.example.b07proj.model.dataCategories.DocumentData
import com.example.b07proj.model.dataCategories.DocumentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DocumentPresenter(
    private val repository: DocumentRepository = DocumentRepository()
) {
    // State flows for UI state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    // State flow for user documents and upload status
    private val _userDocuments = MutableStateFlow<List<Pair<String, DocumentData>>>(emptyList())
    val userDocuments: StateFlow<List<Pair<String, DocumentData>>> = _userDocuments.asStateFlow()

    private val _uploadSuccess = MutableStateFlow<Boolean?>(null)
    val uploadSuccess: StateFlow<Boolean?> = _uploadSuccess.asStateFlow()

    // Fetch user documents from the repository
    fun fetchUserDocuments() {
        _isLoading.value = true
        // get the user documents from the repository then update the state
        repository.getUserDocuments { documents ->
            _userDocuments.value = documents
            _isLoading.value = false
        }
    }

    // Delete a document from the repository
    fun deleteDocument(documentName: String) {
        _isLoading.value = true
        repository.deleteDocument(documentName) { success ->
            if (success) {
                // If deletion was successful, refresh the list to update the UI
                fetchUserDocuments()
            } else {
                // If it failed, just stop the loading indicator.
                _isLoading.value = false
            }
        }
    }
    // Upload a document to the repository
    fun uploadAndSaveDocument(
        fileUri: Uri,
        documentName: String,
        documentDescription: String,
        relevantDate: String
    ) {
        _isLoading.value = true
        // Call the simplified function and provide a callback
        repository.uploadAndSaveDocument(fileUri, documentName, documentDescription, relevantDate) { success ->
            _uploadSuccess.value = success
            _isLoading.value = false
            if (success) {
                // Refresh the document list on successful upload
                fetchUserDocuments()
            }
        }
    }

    // Reset the upload status when it's no longer needed
    fun resetUploadStatus() {
        _uploadSuccess.value = null
    }
}