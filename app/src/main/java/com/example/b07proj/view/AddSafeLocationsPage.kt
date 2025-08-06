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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.b07proj.R
import com.example.b07proj.model.Question
import com.example.b07proj.presenter.dataItems.AddDataItemContract
import com.example.b07proj.presenter.dataItems.AddDataItemPresenter
import com.example.b07proj.presenter.dataItems.Categories
import com.example.b07proj.ui.theme.backgroundAccent

// Renderer for add safe locations page
@Composable
fun RenderAddSafeLocationsPage(navController: NavHostController) {
    AddSafeLocationsPage(navController)
}

@Composable
fun AddSafeLocationsPage(navController: NavHostController) {
    val answers = remember { mutableStateMapOf<String, String>() }
    // To determine if we show the spinner or not
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val presenter : AddDataItemContract.Presenter = remember { AddDataItemPresenter(null) }

    // get locationId from navigation arguments, note if will be null if we are adding
    val locationId = navController.currentBackStackEntry?.arguments?.getString("dataItemId")
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
    LaunchedEffect(key1 = locationId) {
        if (locationId != null) {
            presenter.loadDataItemDetails(Categories.SAFE_LOCATIONS, locationId)
        }
    }
    // Question objects for each questions for safe location
    // Ask for address of safe location as freeform string
    val freeformQuestion1 = Question(
        id = 670,
        question = "What is the address of the safe location?",
        type = "freeform",
        variable = "safeLocationAddress"
    )
    // Ask for description of safe location as freeform string
    val freeformQuestion2 = Question(
        id = 671,
        question = "Describe the safe location",
        type = "freeform",
        variable = "safeLocationDescription",
    )
    val freeformQuestion3 = Question(
        id = 672,
        question = "What is the name of the safe location?",
        type = "freeform",
        variable = "safeLocationName",
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
                ScreenHeaderTop(stringResource(R.string.addSafeLocationHeader))

                // First question for safe location address
                FreeformQuestion2(
                    question = freeformQuestion1,
                    value = answers[freeformQuestion1.variable!!].orEmpty(),
                    onValueChange = { newText -> answers[freeformQuestion1.variable] = newText },
                    label = "Address"
                )

                // Second question for safe location description
                FreeformQuestion2(
                    question = freeformQuestion2,
                    value = answers[freeformQuestion2.variable!!].orEmpty(),
                    onValueChange = { newText -> answers[freeformQuestion2.variable] = newText },
                    label = "Description"
                )

                // third question for safe location name
                FreeformQuestion2(
                    question = freeformQuestion3,
                    value = answers[freeformQuestion3.variable!!].orEmpty(),
                    onValueChange = { newText -> answers[freeformQuestion3.variable] = newText },
                    label = "Name"
                )

                // Submission / Cancel button
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BackButton(
                        navController, "Cancel",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(0.25f))

                    Button(
                        enabled = answers.size == 3 && answers.all { item -> item.value.isNotEmpty() },
                        onClick = {
                            Log.d("AddSafeLocationsPage", "Valid answers: $answers")
                            presenter.saveDataItem(
                                Categories.SAFE_LOCATIONS,
                                answers.toMap(),
                                locationId
                            )
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp).weight(1f)
                    ) {
                        AddEditOrCancelRow(locationId)
                    }
                }
            }
        }
        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}