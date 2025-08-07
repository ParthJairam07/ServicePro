package com.example.b07proj.model

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QuizSaveData {
    // function to get the quiz data by loading it from the JSON file
    fun getQuizData(context: Context): QuizData {
        return QuizDataSource.loadQuizData(context)
    }

    // function to save the quiz responses to the database
    fun saveQuizResponses(
        responses: Map<String, Any>,
        collectionName: String,
        onComplete: (Boolean) -> Unit
    ) {
        val currentUserId = HandleAuth.currentUserUuid
        // saves the responses to the firestore
        if (currentUserId != null && responses.isNotEmpty()) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUserId)
                .collection("quiz_responses")
                .document(collectionName)
                .set(responses)
                .addOnSuccessListener {
                    println("Successfully saved $collectionName responses: $responses")
                    onComplete(true)
                }
                .addOnFailureListener { e ->
                    println("Error saving $collectionName responses: ${e.message}")
                    onComplete(false)
                }
        } else {
            println("Save failed: User is null or responses are empty")
            onComplete(false)
        }
    }
}