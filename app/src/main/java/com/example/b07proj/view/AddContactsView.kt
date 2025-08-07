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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.b07proj.R
import com.example.b07proj.model.Question
import com.example.b07proj.presenter.dataItems.AddDataItemContract
import com.example.b07proj.presenter.dataItems.AddDataItemPresenter
import com.example.b07proj.presenter.dataItems.Categories
import com.example.b07proj.ui.theme.backgroundAccent


@Composable
fun RenderAddContactsPage(navController: NavHostController) {
    AddContactsPage(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactsPage(navController: NavHostController) {
    // our answers for each question are stored here
    val answers = remember { mutableStateMapOf<String, String>() }
    // mapping the question to if a specific error it came across from user input (ex invalid email format)
    val errors = remember { mutableStateMapOf<String, Boolean>() }
    // To determine if we show the spinner or not when fetching/storing data
    var isLoading by remember { mutableStateOf(false) }
    // for Android OS
    val context = LocalContext.current

    // presenter type of AddContactsContract.Presenter (interface), call constructor with null (meaning there is no view yet to pair with)
    val presenter : AddDataItemContract.Presenter = remember { AddDataItemPresenter(null) }


    // get contactId from navigation arguments, note if will be null if we are adding
    val contactId = navController.currentBackStackEntry?.arguments?.getString("dataItemId")
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
    // connecting presenter and view together (before presenter had view as null)
    DisposableEffect(presenter) {
        (presenter as AddDataItemPresenter).view = view
        onDispose {
            presenter.onViewDestroyed()
        }
    }
    // launch effect to fetch data when screen was opened in edit mode
    LaunchedEffect(key1 = contactId) {
        if (contactId != null) {
            presenter.loadDataItemDetails(Categories.EMERGENCY_CONTACTS, contactId)
        }
    }

    // Question objects for each questions for emergency contacts
    val freeformQuestion1 = Question(
        id = 666,
        question = "What is the emergency contact's name?",
        type = "freeform",
        variable = "contactName"
    )
    val phoneNumberQuestion2 = Question(
        id = 667,
        question = "Provide the primary phone of the contact",
        type = "phoneNumber",
        variable = "contactPhoneNumber"
    )
    val dropdownQuestion3 = Question(
        id = 668,
        question = "What is your relationship to this person?",
        type = "dropdown",
        variable = "contactRelation",
        options = listOf("Spouse", "Partner", "Parent", "Child", "Sibling", "Friend", "Guardian", "Caregiver", "Doctor")
    )
    val emailQuestion4 = Question(
        id = 669,
        question = "What is the email address for this contact?",
        type = "email",
        variable = "contactEmail"
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
                ScreenHeaderTop(stringResource(R.string.addContactHeader))

                // First question, free form question
                FreeformQuestion2(
                    question = freeformQuestion1,
                    value = answers[freeformQuestion1.variable!!].orEmpty(),
                    // answers["contact_name"]:(users answer)
                    onValueChange = { newText -> answers[freeformQuestion1.variable] = newText },
                    label = "Name"
                )

                // Contact primary phone number
                PhoneNumberQuestion(
                    question = phoneNumberQuestion2,
                    value = answers[phoneNumberQuestion2.variable!!].orEmpty(),
                    // answers["contact_phone_number"]:(users answer)
                    onValueChange = { newText -> answers[phoneNumberQuestion2.variable] = newText
                    },
                    isError = errors[phoneNumberQuestion2.variable] == true
                )

                // question for relation to user
                DropdownQuestion(
                    question = dropdownQuestion3,
                    onAnswer = { newText -> answers[dropdownQuestion3.variable as String] = newText }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // question for providing email address of contact
                EmailQuestion(
                    question = emailQuestion4,
                    value = answers[emailQuestion4.variable!!].orEmpty(),
                    onValueChange = { newText ->
                        answers[emailQuestion4.variable] = newText
                    },
                    isError = errors[emailQuestion4.variable] == true
                )

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
                        enabled = answers.size == 4 && answers.all { item -> item.value.isNotEmpty() },
                        onClick = {
                            // get answer phone number and email (we need to check them)
                            val phoneNumber = answers[phoneNumberQuestion2.variable].orEmpty()
                            val emailAddress = answers[emailQuestion4.variable].orEmpty()

                            val isPhoneValid = isPhoneNumberValid(phoneNumber)
                            val isEmailValid = isEmailValid(emailAddress)

                            // update errors boolean values at each question.variable
                            // ex: if isPhoneNumberValid was false then errors["phoneNumber"] should be true
                            errors[phoneNumberQuestion2.variable] = !isPhoneValid
                            errors[emailQuestion4.variable] = !isEmailValid

                            // we only proceed if both are valid ie both true
                            if (isPhoneValid && isEmailValid ) {
                                Log.d("AddContactsPage", "Valid phone number and email: $answers")
                                presenter.saveDataItem(Categories.EMERGENCY_CONTACTS, answers.toMap(), contactId)
                            }
                            else {
                                Log.d("AddContactsPage", "Valid phone number or email incorrect logic: $answers")
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp).weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween

                        ) {
                            AddEditOrCancelRow(contactId)
                        }
//                        // change button text depending on if we editing or adding contact
//                        val buttonText = if (contactId == null) {
//                            "Add Contact"
//                        } else {
//                            "Update Contact"
//                        }
//                        Text(buttonText)
                    }
                }
            }
        }
        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}