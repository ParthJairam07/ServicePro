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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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

@Composable
fun RenderAddContactsPage(navController: NavHostController) {
    AddContactsPage(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactsPage(navController: NavHostController) {
    val answers = remember { mutableStateMapOf<String, String>() }
    val errors = remember { mutableStateMapOf<String, Boolean>() }
    // To determine if we show the spinner or not
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val presenter : AddContactsContract.Presenter = remember { AddContactsPresenter(null) }


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
        }
    }
    // connecting presenter and view together
    DisposableEffect(presenter) {
        (presenter as AddContactsPresenter).view = view
        onDispose {
            presenter.onViewDestroyed()
        }
    }

    // Question objects for each questions for emergency contacts
    val freeformQuestion1 = Question(
        id = 666,
        question = "What is the name of the emergency contact?",
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
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
            ) {
                // header
                Text(
                    text = stringResource(id = R.string.addContactHeader),
                    color = backgroundAccent,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = myFont,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                // First question, free form question
                FreeformQuestion2(
                    question = freeformQuestion1,
                    value = answers[freeformQuestion1.variable!!].orEmpty(),
                    // answers["contact_name"]:(users answer)
                    onValueChange = { newText -> answers[freeformQuestion1.variable] = newText }
                )
                Spacer(modifier = Modifier.height(24.dp))
                // Contact primary phone number
                PhoneNumberQuestion(
                    question = phoneNumberQuestion2,
                    value = answers[phoneNumberQuestion2.variable!!].orEmpty(),
                    // answers["contact_phone_number"]:(users answer)
                    onValueChange = { newText -> answers[phoneNumberQuestion2.variable] = newText
                    },
                    isError = errors[phoneNumberQuestion2.variable] == true
                )
                Spacer(modifier = Modifier.height(24.dp))
                // question for relation to user
                DropdownQuestion(
                    question = dropdownQuestion3,
                    onAnswer = { newText -> answers[dropdownQuestion3.variable as String] = newText }
                )
                Spacer(modifier = Modifier.height(24.dp))
                // question for providing email address of contact
                EmailQuestion(
                    question = emailQuestion4,
                    value = answers[emailQuestion4.variable!!].orEmpty(),
                    onValueChange = { newText ->
                        answers[emailQuestion4.variable] = newText
                    },
                    isError = errors[emailQuestion4.variable] == true
                )
                Spacer(modifier = Modifier.height(24.dp))

                // submission button
                Button(
                    onClick = {
                        val phoneNumber = answers[phoneNumberQuestion2.variable].orEmpty()
                        val emailAddress = answers[emailQuestion4.variable].orEmpty()
                        val isPhoneValid = isPhoneNumberValid(phoneNumber)
                        val isEmailValid = isEmailValid(emailAddress)
                        // update errors boolean values at each question.variable
                        errors[phoneNumberQuestion2.variable] = !isPhoneValid
                        errors[emailQuestion4.variable] = !isEmailValid

                        // we only proceed if both are valid ie true
                        if (isPhoneValid && isEmailValid ) {
                            Log.d("AddContactsPage", "Valid phone number and email: $answers")
                            presenter.addContact(answers.toMap())
                        }
                        else {
                            Log.d("AddContactsPage", "Valid phone number or email incorrect logic: $answers")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text("Add Contact")
                }
            }
        }
        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}