package com.example.b07proj.model

import android.content.Context
import com.example.b07proj.model.QuizDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QuizSaveData {
    fun getQuizData(context: Context): QuizData {
        return QuizDataSource.loadQuizData(context)
    }

    fun saveQuizDataWarmup(responses: Map<String, Any>, onComplete: (Boolean) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        println("User: ${user?.uid}, ${user?.email}")
        println("Responses: $responses")
        if (user != null && responses.isNotEmpty()) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)
                .collection("quiz_responses")
                .document("warmup")
                .set(responses)
                .addOnSuccessListener {
                    onComplete(true)
                }
                .addOnFailureListener { e ->
                    println("Error saving quiz responses: ${e.message}")
                    onComplete(false)
                }
        } else {
            onComplete(false)
        }
    }
}