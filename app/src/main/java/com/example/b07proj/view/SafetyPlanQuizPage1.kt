package com.example.b07proj.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.example.b07proj.model.Question
import com.example.b07proj.presenter.QuizPresenter
import com.example.b07proj.ui.theme.BackgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.launch



@Composable
fun SafetyPlanQuizPage1(navController: NavHostController) {
    val context = LocalContext.current
    val presenter = remember { QuizPresenter() }
    SafetyPlanQuizScreen(navController, presenter)
}

@Composable
fun SafetyPlanQuizScreen(navController: NavHostController, presenter: QuizPresenter) {
    var visibleQuestionIndices by remember { mutableStateOf(listOf(1)) }
    var showFollowUp by remember { mutableStateOf(false) }
    val responses = remember { mutableStateMapOf<String, Any>() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val quizData = presenter.getQuizData(LocalContext.current)
    val questions = quizData.questions.Warmup

    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(vertical = 12.dp)
                    .padding(top = 24.dp)
                    .fillMaxWidth()
            ) {
                Text("Logo goes here!")
                Row {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(36.dp, 32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = BackgroundColor
                        )
                    }
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(36.dp, 32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile",
                            tint = BackgroundColor
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            visibleQuestionIndices.forEach { questionIndex ->
                val questionKey = "question$questionIndex"
                val question = questions[questionKey]
                if (question != null) {
                    when (question.type) {
                        "radio" -> RadioQuestion(
                            question = question,
                            onAnswer = { answer ->
                                if (question.id == 5 && answer == "Yes") {
                                    responses["5"] = mutableMapOf("hasChildren" to answer)
                                    showFollowUp = true
                                } else {
                                    responses[question.id.toString()] = answer
                                    showFollowUp = false
                                    val nextIndex = questionIndex + 1
                                    if (questions.containsKey("question$nextIndex") &&
                                        !visibleQuestionIndices.contains(nextIndex)
                                    ) {
                                        visibleQuestionIndices = visibleQuestionIndices + nextIndex
                                    }
                                }
                            }
                        )
                        "dropdown" -> DropdownQuestion(
                            question = question,
                            onAnswer = { answer ->
                                responses[question.id.toString()] = answer
                                val nextIndex = questionIndex + 1
                                if (questions.containsKey("question$nextIndex") &&
                                    !visibleQuestionIndices.contains(nextIndex)
                                ) {
                                    visibleQuestionIndices = visibleQuestionIndices + nextIndex
                                }
                            }
                        )
                        "freeform" -> FreeformQuestion(
                            question = question,
                            onAnswer = { answer ->
                                responses[question.id.toString()] = answer
                                val nextIndex = questionIndex + 1
                                if (questions.containsKey("question$nextIndex") &&
                                    !visibleQuestionIndices.contains(nextIndex)
                                ) {
                                    visibleQuestionIndices = visibleQuestionIndices + nextIndex
                                }
                            }
                        )
                        // You can add more types here if needed like "checkbox", "date"
                        else -> Text("Unsupported question type: ${question.type}")
                    }

                    // Follow-up question for question 5
                    if (question.id == 5 && showFollowUp) {
                        val followUp = question.followUp?.get("Yes")
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
                                    val existing = responses["5"] as? MutableMap<String, Any> ?: mutableMapOf()
                                    existing["codeWord"] = answer
                                    responses["5"] = existing
                                    showFollowUp = false
                                    val nextIndex = questionIndex + 1
                                    if (questions.containsKey("question$nextIndex") &&
                                        !visibleQuestionIndices.contains(nextIndex)
                                    ) {
                                        visibleQuestionIndices = visibleQuestionIndices + nextIndex
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (visibleQuestionIndices.size == questions.size && !showFollowUp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Done(navController, responses) { resps ->
                        presenter.saveResponses(resps, "warmup") { success ->
                            if (success) {
                                navController.navigate("safetyPlanQuizPage2")
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



@Preview(showBackground = true, name = "Safety Plan Quiz Preview")
@Composable
fun SafetyPlanQuizPreview() {
    SafetyPlanQuizPage1(navController = rememberNavController())
}