package com.example.b07proj.view

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.example.b07proj.model.Question
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b07proj.R
import com.example.b07proj.ui.theme.backgroundAccent


@Composable
fun RenderAddDocumentsPage(navController: NavHostController) {
    AddDocumentsPage(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDocumentsPage(navController: NavHostController) {
    val freeformQuestion1 = Question(
        id = 999,
        question = "What is the name of the document?",
        type = "freeform",
        variable = "document_name",
        options = null,
        followUp = null
    )
    val freeformQuestion2 = Question(
        id = 1000,
        question = "Give the document an description.",
        type = "freeform",
        variable = "document_description",
        options = null,
        followUp = null
    )
    val freeformQuestion3 = Question(
        id = 1000,
        question = "Choose a relevant date for the document.",
        type = "date",
        variable = "document_relevant_date",
        options = null,
        followUp = null
    )
    var submittedAnswers by remember { mutableStateOf(mapOf<String, String>()) }

    LoggedInTopBar(navController) {

        val myFont = FontFamily(Font(R.font.afacad))
        Text(
            text = stringResource(R.string.addDocumentHeader),
            color = backgroundAccent,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = myFont,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

        FreeformQuestion(
            question = freeformQuestion1,
            onAnswer = { answerText ->
                Log.d("UploadFileAnswer1", "Answer: $answerText")
                submittedAnswers = submittedAnswers + (freeformQuestion1.variable!! to answerText)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        FreeformQuestion(
            question = freeformQuestion2,
            onAnswer = { answerText ->
                Log.d("UploadFileAnswer2", "Answer: $answerText")
                submittedAnswers = submittedAnswers + (freeformQuestion2.variable!! to answerText)
            }
        )

        DateQuestion(
            question = freeformQuestion3,
            onAnswer = { answerText ->
                Log.d("UploadFileAnswer3", "Answer: $answerText")
                submittedAnswers = submittedAnswers + (freeformQuestion3.variable!! to answerText)
            }
        )

        if (submittedAnswers.isNotEmpty()) {
            Text(
                text = submittedAnswers.toString(),
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}