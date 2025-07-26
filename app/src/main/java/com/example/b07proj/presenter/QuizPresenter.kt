package com.example.b07proj.presenter

import android.content.Context
import com.example.b07proj.model.QuizData
import com.example.b07proj.model.QuizSaveData

class QuizPresenter {
    private val repository = QuizSaveData()
    var prefillMap: Map<String, Any>? = null

    fun getQuizData(context: Context): QuizData {
        return repository.getQuizData(context)
    }

    fun saveResponses(responses: Map<String, Any>, collectionName: String, onComplete: (Boolean) -> Unit) {
        repository.saveQuizResponses(responses, collectionName) { success ->
            onComplete(success)
        }
    }
}