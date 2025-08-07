package com.example.b07proj.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.example.b07proj.model.Question
import com.example.b07proj.presenter.QuizPresenter
import com.example.b07proj.ui.theme.BackgroundColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch


@Composable
fun SafetyPlanQuizPage2(navController: NavHostController) {
    val context = LocalContext.current
    val presenter = remember { QuizPresenter() }
    if (FirebaseAuth.getInstance().currentUser == null) {
        navController.navigate("login")
        return
    }
    LoggedInTopBar(navController) {
        SafetyPlanQuizScreenBranch(navController, presenter)
    }
}

@Composable
fun SafetyPlanQuizScreenBranch(navController: NavHostController, presenter: QuizPresenter) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val responses = remember { mutableStateMapOf<String, Any>() }
    val followUpStates = remember { mutableStateMapOf<String, Boolean>() }
    val selectedStatus = remember { mutableStateOf<String?>(null) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    // Initialize visibleQuestionIndices based on selectedStatus
    var visibleQuestionIndices by remember(selectedStatus.value) {
        mutableStateOf(
            when (selectedStatus.value) {
                "Still in a relationship", "Planning to leave", "Post-separation" -> listOf(1)
                else -> listOf(1)
            }
        )
    }

    // Fetch relationship status from Firestore
    LaunchedEffect(Unit) {
        userId?.let {
            Log.d("SafetyPlanQuizPage2", "Fetching status for user: $it")
            db.collection("users").document(it).collection("quiz_responses").document("warmup")
                .get()
                .addOnSuccessListener { document ->
                    val status = document.getString("1")
                    Log.d("SafetyPlanQuizPage2", "Fetched status: $status")
                    selectedStatus.value = status
                }
                .addOnFailureListener { e ->
                    Log.e("SafetyPlanQuizPage2", "Error fetching status: ${e.message}")
                    scope.launch {
                        snackbarHostState.showSnackbar("Failed to fetch relationship status")
                    }
                }
        } ?: Log.e("SafetyPlanQuizPage2", "User ID is null")
    }

    // Get branch questions based on selectedStatus
    val quizData = presenter.getQuizData(LocalContext.current)
    val questions = selectedStatus.value?.let { status ->
        quizData.questions.BranchQuestions[status]?.also {
            Log.d("SafetyPlanQuizPage2", "Questions for status '$status': ${it.keys}")
        } ?: run {
            Log.e("SafetyPlanQuizPage2", "No BranchQuestions for status: $status")
            emptyMap<String, Question>()
        }
    } ?: emptyMap()
    Log.d("SafetyPlanQuizPage2", "Questions map: $questions")

    // Calculate max index
    val maxIndex = questions.keys.maxOfOrNull { it.removePrefix("question").toInt() } ?: 1
    Log.d("SafetyPlanQuizPage2", "Visible indices: $visibleQuestionIndices, Max index: $maxIndex")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
            ScreenHeader("Safety Plan Questions")
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Info",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Answer these questions to build your safety plan.",
                    style = TextStyle(fontFamily = myFont, fontWeight = FontWeight(600))
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                item {
                    when {
                        selectedStatus.value == null -> {
                            Text(
                                "Loading relationship status...",
                                style = TextStyle(fontFamily = myFont)
                            )
                            Button(
                                onClick = {
                                    userId?.let {
                                        db.collection("users").document(it)
                                            .collection("quiz_responses").document("warmup")
                                            .get()
                                            .addOnSuccessListener { document ->
                                                val status = document.getString("question1")
                                                Log.d(
                                                    "SafetyPlanQuizPage2",
                                                    "Retry fetched status: $status"
                                                )
                                                selectedStatus.value = status
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e(
                                                    "SafetyPlanQuizPage2",
                                                    "Retry error: ${e.message}"
                                                )
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Retry failed")
                                                }
                                            }
                                    }
                                },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text("Retry", style = TextStyle(fontFamily = myFont))
                            }
                        }

                        questions.isEmpty() -> {
                            Text(
                                "No questions available for status: ${selectedStatus.value}",
                                style = TextStyle(fontFamily = myFont)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Done(navController, responses) { responses ->
                                    Log.d(
                                        "SafetyPlanQuizPage2",
                                        "Saving branch responses: $responses"
                                    )
                                    presenter.saveResponses(responses, "branch") { success ->
                                        if (success) {
                                            navController.navigate("safetyPlanQuizPage3")
                                        } else {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Failed to save responses")
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        else -> {
                            // Render all visible questions
                            visibleQuestionIndices.forEach { questionIndex ->
                                val question = questions["question$questionIndex"]
                                Log.d(
                                    "SafetyPlanQuizPage2",
                                    "Rendering question at index $questionIndex: $question"
                                )
                                if (question != null) {
                                    Log.d(
                                        "SafetyPlanQuizPage2",
                                        "Rendering question: ${question.question}, Type: ${question.type}, ID: ${question.id}"
                                    )
                                    when (question.type) {
                                        "radio" -> RadioQuestion(
                                            question = question,
                                            onAnswer = { answer ->
                                                responses[question.id.toString()] = answer
                                                Log.d(
                                                    "SafetyPlanQuizPage2",
                                                    "Radio answer for ID ${question.id}: $answer"
                                                )
                                                if (question.followUp != null && question.followUp.containsKey(
                                                        answer
                                                    )
                                                ) {
                                                    followUpStates["question${question.id}"] = true
                                                } else {
                                                    followUpStates["question${question.id}"] = false
                                                    val nextIndex = questionIndex + 1
                                                    if (questions.containsKey("question$nextIndex") &&
                                                        !visibleQuestionIndices.contains(nextIndex)
                                                    ) {
                                                        visibleQuestionIndices =
                                                            visibleQuestionIndices + nextIndex
                                                        Log.d(
                                                            "SafetyPlanQuizPage2",
                                                            "Added next index: $nextIndex"
                                                        )
                                                    }
                                                }
                                            }
                                        )

                                        "dropdown" -> DropdownQuestion(
                                            question = question,
                                            onAnswer = { answer ->
                                                responses[question.id.toString()] = answer
                                                Log.d(
                                                    "SafetyPlanQuizPage2",
                                                    "Dropdown answer for ID ${question.id}: $answer"
                                                )
                                                val nextIndex = questionIndex + 1
                                                if (questions.containsKey("question$nextIndex") &&
                                                    !visibleQuestionIndices.contains(nextIndex)
                                                ) {
                                                    visibleQuestionIndices =
                                                        visibleQuestionIndices + nextIndex
                                                    Log.d(
                                                        "SafetyPlanQuizPage2",
                                                        "Added next index: $nextIndex"
                                                    )
                                                }
                                            }
                                        )

                                        "freeform" -> FreeformQuestion(
                                            question = question,
                                            onAnswer = { answer ->
                                                responses[question.id.toString()] = answer
                                                Log.d(
                                                    "SafetyPlanQuizPage2",
                                                    "Freeform answer for ID ${question.id}: $answer"
                                                )
                                                val nextIndex = questionIndex + 1
                                                if (questions.containsKey("question$nextIndex") &&
                                                    !visibleQuestionIndices.contains(nextIndex)
                                                ) {
                                                    visibleQuestionIndices =
                                                        visibleQuestionIndices + nextIndex
                                                    Log.d(
                                                        "SafetyPlanQuizPage2",
                                                        "Added next index: $nextIndex"
                                                    )
                                                }
                                            }
                                        )

                                        "date" -> DateQuestion(
                                            question = question,
                                            onAnswer = { answer ->
                                                responses[question.id.toString()] = answer
                                                Log.d(
                                                    "SafetyPlanQuizPage2",
                                                    "Date answer for ID ${question.id}: $answer"
                                                )
                                                val nextIndex = questionIndex + 1
                                                if (questions.containsKey("question$nextIndex") &&
                                                    !visibleQuestionIndices.contains(nextIndex)
                                                ) {
                                                    visibleQuestionIndices =
                                                        visibleQuestionIndices + nextIndex
                                                    Log.d(
                                                        "SafetyPlanQuizPage2",
                                                        "Added next index: $nextIndex"
                                                    )
                                                }
                                            }
                                        )

                                        "checkbox" -> CheckboxQuestion(
                                            question = question,
                                            onAnswer = { answers ->
                                                responses[question.id.toString()] = answers
                                                Log.d(
                                                    "SafetyPlanQuizPage2",
                                                    "Checkbox answer for ID ${question.id}: $answers"
                                                )
                                                val nextIndex = questionIndex + 1
                                                if (questions.containsKey("question$nextIndex") &&
                                                    !visibleQuestionIndices.contains(nextIndex)
                                                ) {
                                                    visibleQuestionIndices =
                                                        visibleQuestionIndices + nextIndex
                                                    Log.d(
                                                        "SafetyPlanQuizPage2",
                                                        "Added next index: $nextIndex"
                                                    )
                                                }
                                            }
                                        )

                                        else -> {
                                            Log.e(
                                                "SafetyPlanQuizPage2",
                                                "Unknown question type: ${question.type}"
                                            )
                                            Text(
                                                "Error: Unknown question type '${question.type}'",
                                                style = TextStyle(
                                                    fontFamily = myFont,
                                                    color = Color.Red
                                                )
                                            )
                                        }
                                    }
                                    // Handle follow-up questions
                                    Log.d(
                                        "SafetyPlanQuizPage2",
                                        "Follow-up states: $followUpStates"
                                    )
                                    if (followUpStates["question${question.id}"] == true && question.followUp != null) {
                                        val answer = responses[question.id.toString()] as? String
                                        val followUp = answer?.let { question.followUp[it] }
                                        Log.d(
                                            "SafetyPlanQuizPage2",
                                            "Follow-up question for answer '$answer': $followUp"
                                        )
                                        if (followUp != null) {
                                            FreeformQuestion(
                                                question = Question(
                                                    id = question.id * 100,
                                                    question = followUp.sub_question,
                                                    type = followUp.input_type,
                                                    variable = followUp.variable,
                                                    options = null,
                                                    followUp = null
                                                ),
                                                onAnswer = { answer ->
                                                    responses[followUp.variable
                                                        ?: "followup${question.id}"] = answer
                                                    Log.d(
                                                        "SafetyPlanQuizPage2",
                                                        "Follow-up answer for ${followUp.variable}: $answer"
                                                    )
                                                    followUpStates["question${question.id}"] = false
                                                    val nextIndex = questionIndex + 1
                                                    if (questions.containsKey("question$nextIndex") &&
                                                        !visibleQuestionIndices.contains(nextIndex)
                                                    ) {
                                                        visibleQuestionIndices =
                                                            visibleQuestionIndices + nextIndex
                                                        Log.d(
                                                            "SafetyPlanQuizPage2",
                                                            "Added next index: $nextIndex"
                                                        )
                                                    }
                                                }
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                } else {
                                    Log.e(
                                        "SafetyPlanQuizPage2",
                                        "Question at index $questionIndex not found in questions map"
                                    )
                                    Text(
                                        "Error: Question $questionIndex not found",
                                        style = TextStyle(fontFamily = myFont, color = Color.Red)
                                    )
                                }
                            }
                            // Log responses before saving
                            Log.d("SafetyPlanQuizPage2", "Responses before Done: $responses")
                            // Show Done button if all questions are visible and no follow-ups are pending
                            if (visibleQuestionIndices.size >= maxIndex && followUpStates.values.all { !it }) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Done(navController, responses) { responses ->
                                        Log.d(
                                            "SafetyPlanQuizPage2",
                                            "Original responses: $responses"
                                        )
                                        // Reformat responses based on status
                                        val reformattedResponses = responses.toMutableMap()
                                        when (selectedStatus.value) {
                                            "Planning to leave" -> {
                                                if (responses["12"] == "Yes" && responses.containsKey(
                                                        "temp_shelter"
                                                    )
                                                ) {
                                                    reformattedResponses["12"] = mapOf(
                                                        "answer" to "Yes",
                                                        "shelter_name" to (responses["temp_shelter"] as String)
                                                    )
                                                    reformattedResponses.remove("temp_shelter")
                                                }
                                            }

                                            "Post-separation" -> {
                                                if (responses["14"] == "Yes" && responses.containsKey(
                                                        "legal_order"
                                                    )
                                                ) {
                                                    reformattedResponses["14"] = mapOf(
                                                        "answer" to "Yes",
                                                        "legal_order" to (responses["legal_order"] as String)
                                                    )
                                                    reformattedResponses.remove("legal_order")
                                                }
                                                if (responses["15"] == "Yes" && responses.containsKey(
                                                        "equipment"
                                                    )
                                                ) {
                                                    reformattedResponses["15"] = mapOf(
                                                        "answer" to "Yes",
                                                        "equipment" to (responses["equipment"] as String)
                                                    )
                                                    reformattedResponses.remove("equipment")
                                                }
                                            }
                                        }
                                        Log.d(
                                            "SafetyPlanQuizPage2",
                                            "Saving branch responses: $reformattedResponses"
                                        )
                                        presenter.saveResponses(
                                            reformattedResponses,
                                            "branch"
                                        ) { success ->
                                            if (success) {
                                                navController.navigate("safetyPlanQuizPage3")
                                            } else {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Failed to save responses")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
