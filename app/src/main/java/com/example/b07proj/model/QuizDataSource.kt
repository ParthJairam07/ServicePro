package com.example.b07proj.model

import android.content.Context
import com.example.b07proj.R
import com.example.b07proj.model.QuizData
import kotlinx.serialization.json.Json
import java.io.IOException

object QuizDataSource {
    private var json = Json { ignoreUnknownKeys = true }

    //runs the function to load the quiz data
    fun loadQuizData(context: Context): QuizData {
        //reads the data from the JSON file
        val jsonString = try {
            context.resources.openRawResource(R.raw.questions)
                .bufferedReader()
                .use { it.readText() }
        } catch (e: Exception) {
            //incase you get error just return everything with empty hashmap
            e.printStackTrace()
            return QuizData(
                Questions(emptyMap(), emptyMap(), emptyMap()),
                Tips(emptyMap(), emptyMap(), emptyMap())
            )
        }
        //converts it to
        return json.decodeFromString(QuizData.serializer(), jsonString)
    }
}