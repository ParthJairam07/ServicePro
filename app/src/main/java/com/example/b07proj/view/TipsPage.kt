package com.example.b07proj.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.b07proj.R
import java.io.InputStreamReader


// get user answers in a usable form for lazyColumn
object AnswersProvider {

    // to help GSON as a blueprint how exactly the JSON is structured
    private data class AnswersWrapper(val answers: Map<String, JsonElement>)

    // return raw JSON user answers
    private fun getAnswersJSON() : String {
        // fake answer for now
        return """
            {
              "answers": {
                "1": "Still in a relationship",
                "2": "Vancouver",
                "3": "Laundry Room",
                "4": "Family",
                "5": {
                  "hasChildren": true,
                  "codeWord": "red robin"
                },
                "6": "Emotional, Financial",
                "7": "Yes",
                "8": "Sarah",
                "16": "Counselling"
              }
            }
        """
    }
    // take JSON answers, returns map
    private fun parseAnswersJSON(jsonString: String) : Map<String, JsonElement> {
        // deserializes the JSON read
        val gson = Gson()
        val wrapper = gson.fromJson(jsonString, AnswersWrapper::class.java)
        return wrapper.answers
    }
    // public function to get user answers
    fun getUserAnswers(): Map<String, JsonElement> {
        val jsonString = getAnswersJSON()
        return parseAnswersJSON(jsonString)
    }
}

// again another blueprint for structure of questions.json
private data class SafetyPlanData(val tips: JsonObject)

// return list of generated tips based on answers
private fun generateTips(planData: SafetyPlanData, userAnswers: Map<String, JsonElement>): List<String> {
    val generatedTips = mutableListOf<String>()
    userAnswers.forEach { (questionId, answerElement) ->
        val tip = findTipForAnswer(
            planData.tips,
            questionId,
            answerElement,
            userAnswers
        )
        if (tip != null) {
            generatedTips.add(tip)
            println("Error when finding correct tip")
        }
    }
    return generatedTips

}
// based on answer, find the tip for that answer
private fun findTipForAnswer(
    tipsRoot: JsonObject,
    questionIdStr: String,
    answerElement: JsonElement,
    allUserAnswers: Map<String, JsonElement>
): String? {
    val (primaryAnswer, followUpValue) = extractAnswerData(questionIdStr, answerElement)
    val tipObject = tipsRoot.getObj(questionIdStr) ?: return null

    return when (questionIdStr) {

        // simple questions that have fixed answer
        "1", "7", "10", "13" -> tipObject.getString(primaryAnswer)
        // questions where their tips has the answer inside of them
        "2" -> tipObject.getString("tip")?.replace("{city}", primaryAnswer)
        "3" -> tipObject.getString("tip")?.replace("{safe_room}", primaryAnswer)
        "6" -> tipObject.getString("tip")?.replace("{abuse_type}", primaryAnswer)
        "8" -> tipObject.getString("tip")?.replace("{contact_name}", primaryAnswer)
        "9" -> tipObject.getString("tip")?.replace("{leave_timing}", primaryAnswer)
        "11" -> tipObject.getString("tip")?.replace("{money_location}", primaryAnswer)
        "16" -> tipObject.getString("tip")?.replace("{support_choice}", primaryAnswer)

        // more complex questions
        "4" -> {
            val tipKey = if (primaryAnswer == "Family" || primaryAnswer == "Roommates") "Family/Roommates" else primaryAnswer
            val safeRoomAnswer = allUserAnswers["3"]?.asString ?: "a safe room"
            tipObject.getString(tipKey)
                ?.replace("{family/roommates}", primaryAnswer)
                ?.replace("{safe_room}", safeRoomAnswer)

        }
        "5" -> {
            val tipTemplate = tipObject.getString(primaryAnswer)
            if (primaryAnswer == "Yes") tipTemplate?.replace("{code_word}", followUpValue ?: "the code word") else tipTemplate
        }
        "12" -> {
            val tipTemplate = tipObject.getString(primaryAnswer)
            if (primaryAnswer == "Yes") tipTemplate?.replace("{temp_shelter}", followUpValue ?: "a safe place") else tipTemplate
        }
        "14" -> {
            val tipTemplate = tipObject.getString(primaryAnswer)
            if (primaryAnswer == "Yes") tipTemplate?.replace("{legal_order}", followUpValue ?: "a legal order") else tipTemplate
        }
        "15" -> {
            val tipTemplate = tipObject.getString(primaryAnswer)
            if (primaryAnswer == "Yes") tipTemplate?.replace("{equipment}", followUpValue ?: "some equipment") else tipTemplate
        }
        else -> null
    }
}
private fun extractAnswerData(questionIdStr: String, answerElement: JsonElement): Pair<String, String?> {
    // for when the answer JSON is just a primitive, simple answer given no follow up
    if (answerElement.isJsonPrimitive) return Pair(answerElement.asString, null)
    // handles logic for when the answer comes in an object
    if (answerElement.isJsonObject) {
        // convert our JSON into object
        val obj = answerElement.asJsonObject
        // switch statement for more complex questions
        return when (questionIdStr) {
            "5" -> Pair(if (obj.get("hasChildren")?.asBoolean == true) "Yes" else "No", obj.get("codeWord")?.asString)
            "12" -> Pair(if (obj.get("hasSafePlace")?.asBoolean == true) "Yes" else "No", obj.get("tempShelter")?.asString)
            "14" -> Pair(if (obj.get("hasProtectionOrder")?.asBoolean == true) "Yes" else "No", obj.get("legalOrder")?.asString)
            "15" -> Pair(if (obj.get("hasSafetyTools")?.asBoolean == true) "Yes" else "No", obj.get("equipment")?.asString)
            else -> Pair("Error: Unknown Object", null)
        }
    }
    return Pair("Error: Unknown Type", null)
}

// extension functions for JsonObject class, return JSON object which contains a key its value is another object
private fun JsonObject.getObj(key: String): JsonObject? =
    if (this.has(key) && this.get(key).isJsonObject) this.getAsJsonObject(key) else null
// get the actual tip associated with the answer (acting as a key)
private fun JsonObject.getString(key: String): String? =
    if (this.has(key) && this.get(key).isJsonPrimitive) this.get(key).asString else null


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipPage(navController: NavHostController) {
    // create a state to hold all tips , default is empty list
    var tipsList by remember { mutableStateOf<List<String>>(emptyList()) }
    // to see if the data is still loading or not
    var isLoading by remember { mutableStateOf(true) }
    // get application context, used to read a file
    val context = LocalContext.current

    // to run this code only once, when the composable first appears on screen
    // only rerun when key1 changes value
    LaunchedEffect(key1 = true) {
        // we want to load the data on a separate thread instead of the UI thread
        val generatedList = withContext(Dispatchers.IO) {
            // open stream
            val inputStream = context.resources.openRawResource(R.raw.questions)

            val dataJson = InputStreamReader(inputStream).readText()
            // use GSON to turn it into structured object
            val planData = Gson().fromJson(dataJson, SafetyPlanData::class.java)

            val userAnswers = AnswersProvider.getUserAnswers()
            generateTips(planData, userAnswers)
        }
        tipsList = generatedList
        isLoading = false

    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Your Personalized Safety Plan") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                // If we are loading, show a spinner.
                item {
                    CircularProgressIndicator()
                }
            } else {
                // Once loading is false, show the list.
                items(tipsList) { tipText ->
                    TipCard(tip = tipText)
                }
            }
        }
    }
}
// display for one tip (for later, make the UI better)
@Composable
fun TipCard(tip: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = tip,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
// used in navigation
@Composable
fun RenderTips(navController: NavHostController) {
    TipPage(navController)
}