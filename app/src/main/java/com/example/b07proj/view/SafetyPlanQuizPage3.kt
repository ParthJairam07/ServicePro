package com.example.b07proj.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import kotlinx.coroutines.launch

@Composable
fun SafetyPlanQuizPage3(navController: NavHostController) {
    val context = LocalContext.current
    val presenter = remember { QuizPresenter() }
    if (FirebaseAuth.getInstance().currentUser == null) {
        navController.navigate("login")
        return
    }
    SafetyPlanQuizScreenFollowUp(navController, presenter)
}

@Composable
fun SafetyPlanQuizScreenFollowUp(navController: NavHostController, presenter: QuizPresenter) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val responses = remember { mutableStateMapOf<String, Any>() }
    val quizData = presenter.getQuizData(LocalContext.current)
    val question = quizData.questions.FollowUp?.get("question1")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
            ScreenHeader("Safety Plan Follow-Up")
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
                    "Answer this final question to complete your safety plan.",
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
                    if (question != null) {
                        Log.d("SafetyPlanQuizPage3", "Rendering question: ${question.question}, Type: ${question.type}, ID: ${question.id}")
                        when (question.type) {
                            "radio" -> RadioQuestion(
                                question = question,
                                onAnswer = { answer ->
                                    responses[question.id.toString()] = answer
                                    Log.d("SafetyPlanQuizPage3", "Radio answer for ID ${question.id}: $answer")
                                }
                            )
                            else -> {
                                Log.e("SafetyPlanQuizPage3", "Unsupported question type: ${question.type}")
                                Text(
                                    "Unsupported question type: ${question.type}",
                                    style = TextStyle(fontFamily = myFont, color = Color.Red)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    } else {
                        Log.e("SafetyPlanQuizPage3", "Follow-up question not found")
                        Text(
                            "Follow-up question not found",
                            style = TextStyle(fontFamily = myFont, color = Color.Red)
                        )
                    }

                    // Show Done button if question is answered
                    if (responses.containsKey("16")) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Done(navController, responses) { responses ->
                                Log.d("SafetyPlanQuizPage3", "Saving follow-up responses: $responses")
                                presenter.saveResponses(responses, "followup") { success ->
                                    if (success) {
                                        navController.navigate("home_page")
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

@Preview(showBackground = true, name = "Safety Plan Quiz Page 3 Preview")
@Composable
fun SafetyPlanQuizPage3Preview() {
    SafetyPlanQuizPage3(navController = rememberNavController())
}