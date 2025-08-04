package com.example.b07proj.model

import android.content.Context
import com.example.b07proj.R
import com.example.b07proj.view.Resource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import org.json.JSONObject


class DirectLinksModel {
    suspend fun loadWarmupCity(): String? {
        val uid = Firebase.auth.currentUser?.uid ?: return null

        val snap = Firebase.firestore
            .collection("users").document(uid)
            .collection("quiz_responses").document("warmup")
            .get().await()

        return snap.getString("2")
    }

    suspend fun loadResources(context: Context): List<Resource> {
        val city = loadWarmupCity()?.trim() ?: return emptyList()

        val json = context.resources.openRawResource(R.raw.services)
            .bufferedReader().use { it.readText() }

        val arr = JSONObject(json).optJSONArray(city) ?: return emptyList()

        return List(arr.length()) { i ->
            arr.getJSONObject(i).run {
                Resource(
                    getString("name"),
                    getString("category"),
                    getString("phone"),
                    getString("url")
                )
            }
        }
    }
}