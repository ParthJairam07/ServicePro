package com.example.b07proj.view

import com.example.b07proj.presenter.DocumentPresenter
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.b07proj.R
import com.example.b07proj.model.Question
import com.example.b07proj.ui.theme.backgroundAccent
import kotlinx.coroutines.launch


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
    //to keep track of the input fields
    var documentName by remember { mutableStateOf("") }
    var documentDescription by remember { mutableStateOf("") }
    var documentDate by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }

    // coroutine scope to launch coroutines
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    // launcher for file picker
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> selectedFileUri = uri }
    )

    val isLoading by presenter.isLoading.collectAsState()
    val uploadSuccess by presenter.uploadSuccess.collectAsState()
    // Observe upload success
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ScreenHeaderTop("Upload File")
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Types of questions
            val nameQuestion = Question(
                id = 999,
                question = "What is the name of the document?",
                type = "freeform",
                variable = "document_name"
            )

            val descQuestion = Question(
                id = 1000,
                question = "Give the document a description.",
                type = "freeform",
                variable = "document_description"
            )

            val dateQuestion = Question(
                id = 1001,
                question = "Choose a relevant date for the document.",
                type = "date",
                variable = "document_relevant_date"
            )

            FreeformQuestion2(
                question = nameQuestion,
                value = documentName,
                onValueChange = { documentName = it },
                label = "Document Name"
            )

            FreeformQuestion2(
                question = descQuestion,
                value = documentDescription,
                onValueChange = { documentDescription = it },
                label = "Description"
            )

            DateQuestion(
                question = dateQuestion,
                onAnswer = { documentDate = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            QuizQuestion("Select a document to upload", required = true)
            Button(onClick = { filePickerLauncher.launch("*/*") }) {
                Text("Select Document", style = TextStyle(fontFamily = myFont))
            }

            // Display selected file name
            selectedFileUri?.let { uri ->
                Text(
                    text = "Selected: ${uri.path?.substringAfterLast('/')}",
                    modifier = Modifier.padding(top = 4.dp),
                    style = TextStyle(fontFamily = myFont)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BackButton(
                        navController, "Cancel",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(0.25f))
                    Button(
                        onClick = {
                            // launch coroutine to upload and save document
                            coroutineScope.launch {
                                presenter.uploadAndSaveDocument(
                                    fileUri = selectedFileUri!!,
                                    documentName = documentName,
                                    documentDescription = documentDescription,
                                    relevantDate = documentDate
                                )
                            }
                        },
                        enabled = selectedFileUri != null && documentName.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp).weight(1f)
                    ) {
                        // We will always be adding, so the text will always be null
                        AddEditOrCancelRow(null)
//                    Text("Upload and Save Entire Document", style = TextStyle(fontFamily = myFont))
                    }
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddDocumentsPagePreview() {
    AddDocumentsPage(rememberNavController())
}