package com.example.b07proj.model.dataCategories

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

data class DocumentData(
    val documentDescription: String = "",
    val relevantDate: String = "",
    val documentUrl: String = ""
)

class DocumentRepository {

    private suspend fun uploadFileToStorage(fileUri: Uri, documentName: String, onComplete: (String?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.e("FirebaseStorage", "Upload failed: User is not authenticated.")
            onComplete(null)
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference
        val safeDocumentName = documentName.replace(Regex("[^a-zA-Z0-9.-]"), "_")
        val fileRef = storageRef.child("documents/${user.uid}/$safeDocumentName")

        try {
            fileRef.putFile(fileUri).await()
            val downloadUrl = fileRef.downloadUrl.await().toString()
            Log.d("FirebaseStorage", "File uploaded successfully: $downloadUrl")
            onComplete(downloadUrl)
        } catch (e: Exception) {
            Log.e("FirebaseStorage", "Error uploading file '$documentName'", e)
            onComplete(null)
        }
    }

    fun deleteDocument(documentName: String, onResult: (Boolean) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.e("Repository", "Delete failed: User not authenticated.")
            onResult(false)
            return
        }

        //  Get references to both Firestore document and Storage file
        val firestoreDocRef = FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.uid)
            .collection("documents")
            .document(documentName)

        // Make sure to use the same sanitized name logic as when you uploaded
        val safeDocumentName = documentName.replace(Regex("[^a-zA-Z0-9.-]"), "_")
        val storageFileRef = FirebaseStorage.getInstance().reference
            .child("documents/${user.uid}/$safeDocumentName")

        // Delete the Firestore document first
        firestoreDocRef.delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Document '$documentName' successfully deleted.")

                // If Firestore deletion is successful, delete the file from Storage
                storageFileRef.delete()
                    .addOnSuccessListener {
                        Log.d("Storage", "File '$safeDocumentName' successfully deleted.")
                        onResult(true) // FINAL SUCCESS
                    }
                    .addOnFailureListener { e ->
                        Log.e("Storage", "Failed to delete file '$safeDocumentName'.", e)
                        // The Firestore entry was deleted, but the file is now orphaned.
                        // For simplicity, we report failure, but in a real app, you might log this for cleanup.
                        onResult(false)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to delete document '$documentName'.", e)
                onResult(false)
            }
    }


    fun uploadAndSaveDocument(
        fileUri: Uri,
        documentName: String,
        documentDescription: String,
        relevantDate: String,
        // callback to signal success or failure.
        onResult: (Boolean) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.e("Firestore", "Save failed: User is null.")
            onResult(false)
            return
        }

        //Get reference to the storage location
        val storageRef = FirebaseStorage.getInstance().reference
        val safeDocumentName = documentName.replace(Regex("[^a-zA-Z0-9.-]"), "_")
        val fileRef = storageRef.child("documents/${user.uid}/$safeDocumentName")

        // Start the upload task
        fileRef.putFile(fileUri)
            .addOnSuccessListener {
                // If upload is successful, get the download URL
                fileRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // If URL is fetched, create the data object
                    val documentData = DocumentData(
                        documentDescription = documentDescription,
                        relevantDate = relevantDate,
                        documentUrl = downloadUrl.toString()
                    )

                    // Save the metadata to Firestore
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(user.uid)
                        .collection("documents")
                        .document(documentName)
                        .set(documentData)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Successfully saved document reference: $documentName")
                            onResult(true) // for success
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error saving document reference '$documentName'", e)
                            onResult(false) // for failure
                        }
                }.addOnFailureListener { e ->
                    Log.e("FirebaseStorage", "Failed to get download URL", e)
                    onResult(false) // for failure
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseStorage", "Error uploading file '$documentName'", e)
                onResult(false) // on failure
            }
    }


    //gets the users documents from firestore
    fun getUserDocuments(
        onResult: (List<Pair<String, DocumentData>>) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.w("Firestore", "Cannot get documents: User is null.")
            onResult(emptyList()) // Return an empty list
            return
        }

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.uid)
            .collection("documents")
            .get()
            .addOnSuccessListener { snapshot ->
                // Map Firestore documents to our DocumentData class
                val docs = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(DocumentData::class.java)?.let { Pair(doc.id, it) }
                }
                onResult(docs) // Return the list of documents
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching user documents.", e)
                onResult(emptyList()) // Return an empty list on failure
            }
    }
}