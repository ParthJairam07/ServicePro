package com.example.b07proj.presenter

import android.net.Uri
import com.example.b07proj.model.DocumentData
import com.example.b07proj.model.DocumentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DocumentPresenter(
    private val repository: DocumentRepository = DocumentRepository()
) {
    // The scope is still useful for other tasks, but not strictly needed for the repository calls anymore.
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userDocuments = MutableStateFlow<List<Pair<String, DocumentData>>>(emptyList())
    val userDocuments: StateFlow<List<Pair<String, DocumentData>>> = _userDocuments.asStateFlow()

    private val _uploadSuccess = MutableStateFlow<Boolean?>(null)
    val uploadSuccess: StateFlow<Boolean?> = _uploadSuccess.asStateFlow()

    fun fetchUserDocuments() {
        _isLoading.value = true
        // Call the simplified function and provide a callback
        repository.getUserDocuments { documents ->
            _userDocuments.value = documents
            _isLoading.value = false
        }
    }

    fun deleteDocument(documentName: String) {
        _isLoading.value = true
        repository.deleteDocument(documentName) { success ->
            if (success) {
                // If deletion was successful, refresh the list to update the UI
                fetchUserDocuments()
            } else {
                // If it failed, just stop the loading indicator.
                // The view can show a Toast.
                _isLoading.value = false
            }
            // You could add another StateFlow here to signal success/failure to the UI for a Toast
        }
    }

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

    fun resetUploadStatus() {
        _uploadSuccess.value = null
    }
}