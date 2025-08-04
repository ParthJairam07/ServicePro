package com.example.b07proj.view

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlinx.coroutines.tasks.await
import com.example.b07proj.R
import org.json.JSONObject
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
data class Resource(
    val name: String?,
    val category: String?,
    val phone: String?,
    val url: String?
)




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



@Composable
fun DirectLinks() {
    val ctx = LocalContext.current
    var city      by remember { mutableStateOf("") }
    var resources by remember { mutableStateOf(emptyList<Resource>()) }

    // fetch once
    LaunchedEffect(Unit) {
        city      = loadWarmupCity().orEmpty()
        resources = loadResources(ctx)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            if (city.isBlank()) "Direct Links to Support Services"
            else               "Support Services in $city",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        resources.forEach { res ->
            if (res.name != null) {
                Column(Modifier.fillMaxWidth()) {
                    Text(
                        res.name,
                        style  = MaterialTheme.typography.titleMedium,
                        color  = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            res.url?.let { ctx.startActivity(Intent(Intent.ACTION_VIEW, it.toUri())) }
                        }
                    )
                    Text(
                        "${res.category} • ${res.phone}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

    }
}

// Wont work as Preview doesnt work with FireBase
@Preview(showBackground = true, name = "Real JSON Preview")
@Composable
fun DirectLinksPagePreview() {
    DirectLinks()  // Now this will actually load your res/raw/services.json
}




