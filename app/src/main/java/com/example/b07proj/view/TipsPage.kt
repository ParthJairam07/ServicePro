package com.example.b07proj.view


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.b07proj.R
import com.example.b07proj.ui.theme.Primary40
import java.io.InputStreamReader
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.b07proj.ui.theme.backgroundAccent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import kotlinx.coroutines.tasks.await

// This page displays the tips of a logged in user


// get user answers in a usable form for lazyColumn
object AnswersProvider {

    // to help GSON as a blueprint how exactly the JSON is structured
    private data class AnswersWrapper(val answers: Map<String, JsonElement>)
    // Get real user JSON user answers, and convert in similar format as getAnswersJSONFake() returns
    private suspend fun getUserQuestionnaireAnswers() : String {
        // using FirebaseAuth to get the profile of user currently signed in
        val user = FirebaseAuth.getInstance().currentUser
        // use gson to format into JSON in a pretty (human readable) way
        val gson = GsonBuilder().setPrettyPrinting().create()

        if (user != null) {
            // Building the path to the data, first top level collection "users"
            try {
                // build a path to collection "quiz_responses" in firebase
                val responses = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.uid)
                    .collection("quiz_responses")
                    .get()
                    .await()
                // making map containing all the answers
                val allAnswersMap = mutableMapOf<String, Any?>()
                // for each document, put its all the data into allAnswersMap
                for (document in responses.documents) {
                    document.data?.let {
                        allAnswersMap.putAll(it)
                    }
                }
                // wrap answers on top of the mapping
                val finalJSONStructure = mapOf("answers" to allAnswersMap)
                // convert to JSON
                return gson.toJson(finalJSONStructure)

            }
            catch (e: Exception) {
                println("Failed to fetch responses: ${e.message}")
                return "{}"
            }
        }
        // if no user then no JSON
        return "{}"
    }

    // take JSON answers, returns map
    private fun parseAnswersJSON(jsonString: String) : Map<String, JsonElement> {
        // deserializes the JSON read
        val gson = Gson()
        val wrapper = gson.fromJson(jsonString, AnswersWrapper::class.java)
        return wrapper.answers
    }
    // public function to get user answers
    suspend fun getUserAnswers(): Map<String, JsonElement> {
        val jsonString = getUserQuestionnaireAnswers()
        return parseAnswersJSON(jsonString)
    }
}

// again another blueprint for structure of questions.json
private data class SafetyPlanData(val tips: JsonObject)

data class TipAndTitle(
    val tip: String,
    val title: String
)

// return list of generated tips based on answers
private fun generateTips(planData: SafetyPlanData, userAnswers: Map<String, JsonElement>): List<TipAndTitle> {
    // a list to store a tip followed by its title
    val generatedPairs  = mutableListOf<TipAndTitle>()
    userAnswers.forEach { (questionId, answerElement) ->
        val tip = findTipForAnswer(
            planData.tips,
            questionId,
            answerElement,
            userAnswers
        )
        if (tip != null) {
            val title = getTipTitle(questionId)
            generatedPairs.add(TipAndTitle(title = title, tip = tip))

        }
    }
    return generatedPairs

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
// return the header string associated to the question id
private fun getTipTitle(questionIdStr: String): String {
    return when (questionIdStr) {
        "1" -> "Stay Prepared"
        "2" -> "Local Support"
        "3" -> "Stock Your Safe Room with Essentials"
        "4" -> "Household Safety"
        "5" -> "Plan Around Your Children"
        "6" -> "Reach Out During Abuse"
        "7" -> "Start Logging Incidents"
        "8" -> "Save Emergency Contacts"
        "9" -> "Plan Your Leaving Time"
        "10" -> "Ready a Go-Bag"
        "11" -> "Secure Emergency Money"
        "12" -> "Confirm a Safe Shelter Option"
        "13" -> "Stay Alert About Any Ongoing Contact"
        "14" -> "Share Your Protection Order"
        "15" -> "Keep Safety Tools Ready"
        "16" -> "Seeking Support"
        else -> "Something went wrong. Sorry!"
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
            "5" -> Pair(if (obj.get("hasChildren")?.asString == "Yes") "Yes" else "No", obj.get("codeWord")?.asString)
            "12" -> Pair(if (obj.get("answer")?.asString == "Yes") "Yes" else "No", obj.get("shelter_name")?.asString)
            "14" -> Pair(if (obj.get("answer")?.asString == "Yes") "Yes" else "No", obj.get("legal_order")?.asString)
            "15" -> Pair(if (obj.get("answer")?.asBoolean == true) "Yes" else "No", obj.get("equipment")?.asString)
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
    // create a state to hold all tipsAndTitle objects, default is empty list
    var tipsList by remember { mutableStateOf<List<TipAndTitle>>(emptyList()) }
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
            // ordered as a column for horizontal line below the header
            Column {
                TopAppBar(
                    title = {
                        // logo of the app
                        Image(
                            painter = painterResource(R.drawable.templogo) ,
                            contentDescription = stringResource(id = R.string.logoDescription),
                        )
                    }
                )
                HorizontalDivider(
                    color = Color.Gray,
                    thickness = 0.5.dp
                )
            }
        }, floatingActionButton = {
            ExitButton(
                modifier = Modifier
                    .padding(5.dp)    // placement
            )
        }
    ) { paddingValues ->
        // used to load all tips in a scrollable view (very similar to recycler view)
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                val myFont = FontFamily(Font(R.font.afacad))
                Text(
                    text = stringResource(R.string.tipsHeader),
                    color = backgroundAccent,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = myFont
                )
                Spacer(modifier = Modifier.height(16.dp))

            }
            if (isLoading) {
                // If we are loading, show a spinner.
                item {
                    CircularProgressIndicator()
                }
            } else {
                // Once loading is false, show the list.
                items(tipsList) { tipText ->
                    TipCard(tipAndTitle = tipText)
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    BackButton(
                        navController = navController
                    )
                }

            }
        }
    }
}
// display for one tip (for later, make the UI better)
@Composable
fun TipCard(tipAndTitle: TipAndTitle) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // header + tip text
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = tipAndTitle.title,
                color = backgroundAccent,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = myFont

            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = tipAndTitle.tip,
                fontFamily = myFont
            )

        }
    }
}
@Composable
fun BackButton(navController: NavHostController) {
    Button(
        onClick = {
            navController.navigate("home_page")
        },
        colors = ButtonDefaults.buttonColors(containerColor = Primary40),
    ) {
        Image(
            painter = painterResource(id = R.drawable.whitearrowgoback),
            contentDescription = stringResource(id = R.string.arrow_content_description),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stringResource(R.string.BackButtonText),

            )
    }
}
// used in navigation
@Composable
fun RenderTips(navController: NavHostController) {
    TipPage(navController)
}