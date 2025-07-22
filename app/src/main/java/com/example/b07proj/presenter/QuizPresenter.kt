package com.example.b07proj.presenter

import android.content.Context
import com.example.b07proj.model.QuizData
import com.example.b07proj.model.QuizSaveData

class QuizPresenter {
    private val repository = QuizSaveData()

    fun getQuizData(context: Context): QuizData {
        return repository.getQuizData(context)
    }

    fun saveResponses(responses: Map<String, Any>, onComplete: () -> Unit) {
        repository.saveQuizDataWarmup(responses) { success ->
            if (success) {
                onComplete()
            }
        }
    }
}