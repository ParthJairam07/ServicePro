package com.example.b07proj.view

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.b07proj.R
import com.example.b07proj.model.Question
import com.example.b07proj.presenter.dataItems.AddDataItemContract
import com.example.b07proj.presenter.dataItems.AddDataItemPresenter
import com.example.b07proj.presenter.dataItems.Categories

// Renderer component for medication page
@Composable
fun RenderAddMedicationPage(navController: NavHostController) {
    AddMedicationPage(navController)
}

@Composable
fun AddMedicationPage(navController: NavHostController) {
    // our answers for each question are stored here
    val answers = remember { mutableStateMapOf<String, String>() }
    // mapping the question to if a specific error it came across from user input (ex invalid email format)
    val errors = remember { mutableStateMapOf<String, Boolean>() }
    // To determine if we show the spinner or not
    var isLoading by remember { mutableStateOf(false) }
    // for Android OS
    val context = LocalContext.current

    // presenter type of AddDataItemContract.Presenter (interface), call constructor with null (meaning there is no view yet to pair with)
    val presenter : AddDataItemContract.Presenter = remember { AddDataItemPresenter(null) }

    // get dataItemId from navigation arguments, note if will be null if we are adding
    val medicationId = navController.currentBackStackEntry?.arguments?.getString("dataItemId")
    // Provide the view contract
    val view = remember {
        object : AddDataItemContract.View {
            override fun showLoading() {
                isLoading = true
            }

            override fun hideLoading() {
                isLoading = false
            }
            override fun navigateBack() {
                navController.popBackStack()
            }

            override fun showSuccess(message: String) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

            override fun showError(message: String) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }

            override fun displayDataItemDetails(itemData: Map<String, String>) {
                // for editing we clear all the previous answers and load in the fetched data
                answers.clear()
                answers.putAll(itemData)
            }
        }
    }
    // connecting presenter and view together
    DisposableEffect(presenter) {
        (presenter as AddDataItemPresenter).view = view
        onDispose {
            presenter.onViewDestroyed()
        }
    }
    // launch effect to fetch data when screen was opened in edit mode
    LaunchedEffect(key1 = medicationId) {
        if (medicationId != null) {
            presenter.loadDataItemDetails(Categories.MEDICATIONS, medicationId)
        }
    }

    // Question objects for each questions for medication
    // Ask for name of medication
    val freeformQuestion1 = Question(
        id = 672,
        question = "What is the name of the medication?",
        type = "freeform",
        variable = "medicationName"
    )

    // Ask for dosage of medication as freeform string
    val freeformQuestion2 = Question(
        id = 673,
        question = "What dosage do you take?",
        type = "freeform",
        variable = "medicationDosage",
    )

    // Ask for expiry of medication as freeform string
    val dateQuestion3 = Question(
        id = 674,
        question = "When does it expire?",
        type = "date",
        variable = "medicationExpiry",
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        // actual UI for page starts here
        LoggedInTopBar(navController) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // header
                ScreenHeaderTop(stringResource(R.string.addMedicationHeader))

                // First question for medication name
                FreeformQuestion2(
                    question = freeformQuestion1,
                    value = answers[freeformQuestion1.variable!!].orEmpty(),
                    onValueChange = { newText -> answers[freeformQuestion1.variable] = newText },
                    label = "Name"
                )

                // Second question for medication dosage
                FreeformQuestion2(
                    question = freeformQuestion2,
                    value = answers[freeformQuestion2.variable!!].orEmpty(),
                    onValueChange = { newText -> answers[freeformQuestion2.variable] = newText },
                    label = "Dosage"
                )

                DateQuestion(
                    question = dateQuestion3,
                    onAnswer = { newText -> answers[dateQuestion3.variable!!] = newText }
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Submission / Cancel button
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BackButton (
                        navController, "Cancel",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer( modifier = Modifier.weight(0.25f) )
                    Button(
                        enabled = answers.size == 3 && answers.all { item -> item.value.isNotEmpty() },
                        onClick = {
                            Log.d("AddMedicationPage", "Valid answers: $answers")
                            presenter.saveDataItem(Categories.MEDICATIONS, answers.toMap(), medicationId)
                        },
                        modifier = Modifier.padding(top = 16.dp).weight(1f),
                    ) {
                        AddEditOrCancelRow(medicationId)
                    }
                }

            }
        }
        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun AddEditOrCancelRow(id: String?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = if (id == null) Icons.Filled.Add else Icons.Filled.Edit,
            contentDescription = "Home",
        )
        Text(
            text = if (id == null) "Add" else "Edit",
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}