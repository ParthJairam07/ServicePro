package com.example.b07proj.view

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.draw.scale


//@Composable
//fun SafetyPlanQuizPageTemp(navController: NavHostController) {
  //  val presenter = remember { QuizPresenter() }
   // SafetyPlanQuizScreen2(navController, presenter)
//}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun SafetyPlanQuizScreen2(
    navController: NavHostController,
    presenter: QuizPresenter
) {
    // State to track current question and responses
    //var currentQuestionIndex by remember { mutableIntStateOf(1) } // Start with question1
    val responses by remember { mutableStateOf(mutableMapOf<String, Any>()) } // Store responses
    var showFollowUp by remember { mutableStateOf(false) } // Track follow-up for question5
    var visibleQuestionIndices by remember { mutableStateOf(listOf(1)) } // starting with question1
    Log.d("VisibleIndices", visibleQuestionIndices.joinToString())
    // Get Warmup questions
    val quizData = presenter.getQuizData(LocalContext.current)
    val questions = quizData.questions.Warmup
//    val currentQuestion = questions["question$currentQuestionIndex"]

    Scaffold(
        // temporary topBar and values for now
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
                    // settings icon to access pin information and more
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
                    // person button to access account info
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
        // columns for the questions
        Column(
            // modifier for the values of sizing and position, padding
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {

            // loop over all visible questions and show them
            visibleQuestionIndices.forEach { questionIndex ->
                val questionKey = "question$questionIndex"
                val question = questions[questionKey]
                Log.d("CheckQuestion", "$questionKey: ${question != null}")
                var showSubmit by remember { mutableStateOf(false) }
                for ((key,value) in outputMap)
                    Log.d(key,"$value"+"e")
                // first case, if the user wants to edit their answers
                if (outputMap.isNotEmpty()) {
                    val targetKey = outputMap.keys.first()
                    val question = questions.values.firstOrNull { it.id.toString() == targetKey }

                    question?.let {
                        // Show the first question (hasChildren)
                        when (it.type) {
                            "radio" -> RadioQuestion(
                                question = it,
                                onAnswer = { answer ->
                                    Log.d(it.id.toString(),it.id.toString())
                                    if (it.id != 5) {
                                        Log.d("hm", "hm")
                                        responses[it.id.toString()] = answer
                                        // Show done button after single question
                                        showSubmit = true

                                    }else {
                                        responses[it.id.toString()] = mutableMapOf("hasChildren" to answer)
                                        showFollowUp = (answer == "Yes") // Only show follow-up if true
                                        if (!showFollowUp) showSubmit = true
                                    }
                                }
                            )
                            // dropdown type question
                            "dropdown" -> DropdownQuestion(
                                question = it,
                                onAnswer = { answer ->
                                    responses[it.id.toString()] = answer
                                    showSubmit = true
                                }
                            )
                            // freeform type question
                            "freeform" -> FreeformQuestion(
                                question = it,
                                onAnswer = { answer ->
                                    responses[it.id.toString()] = answer
                                    showSubmit = true
                                }
                            )
                        }
                        // show the follow-up input (codeWord)
                        if (showFollowUp) {
                            val followUp = it.followUp?.get("Yes")
                            if (followUp != null) {
                                FreeformQuestion2(
                                    question = Question(
                                        id = it.id * 100,
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
                                        showSubmit = true
                                    }
                                )
                            }
                        }
                    }
                }
                // next case, where the questionnaire starts from the beginning
                else {
                    // Loop over all visible questions and show them
                    visibleQuestionIndices.forEach { questionIndex ->
                        val questionKey = "question$questionIndex"
                        val question = questions[questionKey]
                        if (question != null) {
                            when (question.type) {
                                "radio" -> RadioQuestion2(
                                    question = question,
                                    onAnswer = { answer ->
                                        if (question.id == 5 && answer == "Yes") {
                                            responses["5"] = mutableMapOf("hasChildren" to answer) // Start hash map
                                            showFollowUp = true
                                        } else {
                                            Log.d("gmg","gmg")
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
                                "dropdown" -> DropdownQuestion2(
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
                                "freeform" -> FreeformQuestion2(
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
                            }
                            // Follow-up for question 5
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

                    // Show Done button after last question + follow-up handled
                    if (visibleQuestionIndices.size == questions.size && !showFollowUp) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Done(navController, responses) { resps ->
                                presenter.saveResponses(resps) {
                                    navController.navigate("landing_page") // your route
                                }
                            }
                        }
                    }
                }
                //set the Done2 button where the outputMap would be updated and be redirected to the landing_page
                if (outputMap.isNotEmpty() && showSubmit) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Done2(responses) { resps ->
                            presenter.saveResponses(resps) {
                                navController.navigate("landing_page")
                            }
                        }
                    }
                }


            }

            Spacer(modifier = Modifier.height(24.dp))

            // Show Done button after last question + follow-up handled
            if (visibleQuestionIndices.size == questions.size && !showFollowUp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Done2( responses) { resps ->
                        presenter.saveResponses(resps) {
                            navController.navigate("landing_page") // your route
                        }
                    }
                }
            }
        }
    }

}

//@Composable
//fun ScreenHeader2(text: String) {
   // Text(
    //    text = text,
//            fontFamily = myFont,
     //       fontWeight = FontWeight.Bold,
     //       fontSize = 24.sp,
     //       color = Color.Black
     //   ),
     //   modifier = Modifier.padding(vertical = 16.dp)
   // )
//}

@Composable
fun RadioQuestion2(question: Question, onAnswer: (String) -> Unit) {
    var selectedOption by remember { mutableIntStateOf(-1) }
    QuizQuestion2(question.question, required = true)
    question.options?.forEachIndexed { index, text ->
        QuizRadioOption2(
            text = text,
            selected = selectedOption == index,
            onClick = {
                selectedOption = index
                onAnswer(text)
            }
        )
    }
}

@Composable
fun DropdownQuestion2(question: Question, onAnswer: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }
    QuizQuestion2(question.question, required = true)
    Box {
        Button(onClick = { expanded = true }) {
            Text(
                selectedOption.ifEmpty { "Select an option" },
                style = TextStyle(fontFamily = myFont)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            question.options?.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = TextStyle(fontFamily = myFont)) },
                    onClick = {
                        selectedOption = option
                        expanded = false
                        onAnswer(option)
                    }
                )
            }
        }
    }
}

@Composable
fun FreeformQuestion2(question: Question, onAnswer: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    QuizQuestion2(question.question, required = true)
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(fontFamily = myFont)
    )
    Button(
        onClick = { if (text.isNotEmpty()) onAnswer(text) },
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Text("Submit", style = TextStyle(fontFamily = myFont))
    }
}

@Composable
fun QuizQuestion2(text: String, required: Boolean = false) {
    Text(
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontFamily = myFont, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)) {
                append(text)
            }
            if (required) {
                withStyle(style = SpanStyle(fontFamily = myFont, fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 16.sp)) {
                    append(" *")
                }
            }
        },
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun QuizRadioOption2(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            modifier = Modifier
                .size(28.dp)
                .scale(0.75f),
            onClick = onClick,
            selected = selected
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = TextStyle(fontFamily = myFont, fontWeight = FontWeight(500))
        )
    }
}

@Composable
fun Done2(
    responses: Map<String, Any>,
    onDoneClicked: (Map<String, Any>) -> Unit
) {
    Button(
        onClick = { onDoneClicked(responses) },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Done", style = TextStyle(fontFamily = myFont))
    }
}

