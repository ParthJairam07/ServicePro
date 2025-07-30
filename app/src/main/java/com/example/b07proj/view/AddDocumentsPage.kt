package com.example.b07proj.view

import com.example.b07proj.presenter.DocumentPresenter
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.b07proj.R
import com.example.b07proj.model.DocumentRepository
import com.example.b07proj.model.Question
import com.example.b07proj.ui.theme.backgroundAccent
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun RenderAddDocumentsPage(navController: NavHostController) {
    AddDocumentsPage(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDocumentsPage(
    navController: NavHostController,
    presenter: DocumentPresenter = remember { DocumentPresenter() }
) {
    var submittedAnswers by remember { mutableStateOf(mapOf<String, String>()) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> selectedFileUri = uri }
    )

    // Observe presenter state as Compose state
    val isLoading by presenter.isLoading.collectAsState()
    val uploadSuccess by presenter.uploadSuccess.collectAsState()

    // React to upload result
    LaunchedEffect(uploadSuccess) {
        uploadSuccess?.let { success ->
            if (success) {
                Toast.makeText(context, "Document saved successfully!", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            } else {
                Toast.makeText(context, "Error saving document.", Toast.LENGTH_LONG).show()
            }
            presenter.resetUploadStatus()
        }
    }

    LoggedInTopBar(navController) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.addDocumentHeader),
                color = backgroundAccent,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = myFont,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))

            val freeformQuestion1 = Question(
                id = 999,
                question = "What is the name of the document?",
                type = "freeform",
                variable = "document_name"
            )
            val freeformQuestion2 = Question(
                id = 1000,
                question = "Give the document a description.",
                type = "freeform",
                variable = "document_description"
            )
            val freeformQuestion3 = Question(
                id = 1001,
                question = "Choose a relevant date for the document.",
                type = "date",
                variable = "document_relevant_date"
            )
            Spacer(modifier = Modifier.height(8.dp))
            FreeformQuestion(
                question = freeformQuestion1,
                onAnswer = { answerText ->
                    submittedAnswers = submittedAnswers + (freeformQuestion1.variable!! to answerText)
                }
            )

            FreeformQuestion(
                question = freeformQuestion2,
                onAnswer = { answerText ->
                    submittedAnswers = submittedAnswers + (freeformQuestion2.variable!! to answerText)
                }
            )

            DateQuestion(
                question = freeformQuestion3,
                onAnswer = { answerText ->
                    submittedAnswers = submittedAnswers + (freeformQuestion3.variable!! to answerText)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            //file picker
            Button(onClick = { filePickerLauncher.launch("*/*") }) {
                Text("Select Document", style = TextStyle(fontFamily = myFont))
            }
            selectedFileUri?.let { uri ->
                Text(
                    text = "Selected: ${uri.path?.substringAfterLast('/')}",
                    modifier = Modifier.padding(top = 4.dp),
                    style = TextStyle(fontFamily = myFont)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Loading indicator and submit button
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                val documentName = submittedAnswers["document_name"]
                Button(
                    onClick = {
                        coroutineScope.launch {
                            presenter.uploadAndSaveDocument(
                                fileUri = selectedFileUri!!,
                                documentName = submittedAnswers["document_name"]!!,
                                documentDescription = submittedAnswers["document_description"] ?: "",
                                relevantDate = submittedAnswers["document_relevant_date"] ?: ""
                            )
                        }
                    },
                    enabled = selectedFileUri != null && !submittedAnswers["document_name"].isNullOrBlank()
                ) {
                    Text("Upload and Save Entire Document", style = TextStyle(fontFamily = myFont))
                }

                if (isLoading) {
                    CircularProgressIndicator()
                }
            }

        }
    }
}
