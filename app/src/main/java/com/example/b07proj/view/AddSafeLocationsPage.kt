package com.example.b07proj.view

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.b07proj.presenter.contacts.AddContactsContract
import com.example.b07proj.presenter.contacts.AddContactsPresenter
import com.example.b07proj.ui.theme.backgroundAccent

// Renderer for add safe locations page
@Composable
fun RenderAddSafeLocationsPage(navController: NavHostController) {
    AddSafeLocationsPage(navController)
}

@Composable
fun AddSafeLocationsPage(navController: NavHostController) {
    val answers = remember { mutableStateMapOf<String, String>() }
    val errors = remember { mutableStateMapOf<String, Boolean>() }
    // To determine if we show the spinner or not
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val presenter : AddContactsContract.Presenter = remember { AddContactsPresenter(null) }

    // get contactId from navigation arguments, note if will be null if we are adding
    val contactId = navController.currentBackStackEntry?.arguments?.getString("contactId")
    // Provide the view contract
    val view = remember {
        object : AddContactsContract.View {
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

            override fun displayContactDetails(contactData: Map<String, String>) {
                // for editing we clear all the previous answers and load in the fetched data
                answers.clear()
                answers.putAll(contactData)
            }
        }
    }
    // connecting presenter and view together
    DisposableEffect(presenter) {
        (presenter as AddContactsPresenter).view = view
        onDispose {
            presenter.onViewDestroyed()
        }
    }
    // launch effect to fetch data when screen was opened in edit mode
    LaunchedEffect(key1 = contactId) {
        if (contactId != null) {
            presenter.loadContactDetails(contactId)
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
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
            ) {
                // header
                Text(
                    text = stringResource(id = R.string.addSafeLocationHeader),
                    color = backgroundAccent,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = myFont,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                // First question for safe location address
                FreeformQuestion2(
                    question = freeformQuestion1,
                    value = answers[freeformQuestion1.variable!!].orEmpty(),
                    // answers["contact_name"]:(users answer)
                    onValueChange = { newText -> answers[freeformQuestion1.variable] = newText },
                    label = "Address"
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Second question for safe location description
                FreeformQuestion2(
                    question = freeformQuestion2,
                    value = answers[freeformQuestion2.variable!!].orEmpty(),
                    // answers["contact_phone_number"]:(users answer)
                    onValueChange = { newText -> answers[freeformQuestion2.variable] = newText },
                    label = "Description"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // third question for safe location name
                FreeformQuestion2(
                    question = freeformQuestion3,
                    value = answers[freeformQuestion3.variable!!].orEmpty(),
                    // answers["contact_phone_number"]:(users answer)
                    onValueChange = { newText -> answers[freeformQuestion3.variable] = newText },
                    label = "Name"
                )
                Spacer(modifier = Modifier.height(24.dp))

                // submission button
                Button(
                    onClick = {
//                        val address = answers[freeformQuestion1.variable].orEmpty()
//                        val description = answers[freeformQuestion2.variable].orEmpty()

                        // we only proceed if both are valid ie true
                        // TODO: Since we arent using a phone number or email, we don't need the below check!
                        if (true) {
                            Log.d("AddSafeLocationsPage", "Valid answers: $answers")
                            presenter.saveContact(answers.toMap(), contactId)
                        }
                        else {
                            Log.d("AddSafeLocationsPage", "Valid phone number or email incorrect logic: $answers")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text("Add Safe Location")
                }
            }
        }
        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}