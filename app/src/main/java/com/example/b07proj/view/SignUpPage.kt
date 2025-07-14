package com.example.b07proj.view

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.example.b07proj.R
import com.example.b07proj.model.HandleAuth
import com.example.b07proj.presenter.AuthPresenter
import com.example.b07proj.ui.theme.Primary50
import com.example.b07proj.ui.theme.backgroundAccent

// page for user to sign up, pass in navController to switch pages
@Composable
fun SignUpPage(navController: NavHostController) {
    UISignUpPage(navController)
}
// set a function to change the default radio button features to custom ones, of type RadioButtonColors
@Composable
fun accentRadioColors(): RadioButtonColors {
    return RadioButtonDefaults.colors(
        selectedColor = backgroundAccent,
        unselectedColor = Color.Gray
    )
}

// let the compiler know to use APIs from material3 UI, build the UI
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UISignUpPage(navController: NavHostController) {
    val context = LocalContext.current
    // create presenter to handle authentication, i.e what page to show on success etc
    val presenter = remember {
        AuthPresenter(
            auth = HandleAuth(),
            view = object : SignUpView {
                // override function onSignUpSuccess to navigate to landing_page once successful
                override fun onSignUpSuccess() {
                    navController.navigate("landing_page")
                }
                // override a method to display an error message to the user
                override fun showError(message: String) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // create a Scaffold to house the TopAppBar and other parts of the page
    Scaffold(
        // create a topBar element which will consist of the logo
        topBar = {
            TopAppBar(
                // set the colors to the default values that match the app
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    // set the logo through the Image() element and enter the correct description
                    Image(
                        painter = painterResource(R.drawable.templogo),
                        contentDescription = stringResource(id = R.string.logoDescription),
                    )
                }
            )
        }, floatingActionButton = {
            ExitButton(
                modifier = Modifier
                    .padding(5.dp)    // placement
            )
        }
    ) { padding ->
        // create a column to fill out the entire background page and house the necessary details for signup
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(16.dp)
        ) {
            // call the SignupTitle function to get the page title
            SignUpTitle()
            // call the SignUpForm function and pass in presenter in order to move onto the next pages
            SignUpForm(presenter)
        }
    }
}


// signup title heading
@Composable
fun SignUpTitle() {
    Text(
        // set the text colors, padding, font and so on
        text = stringResource(R.string.signup_title),
        modifier = Modifier
            .padding(start = 30.dp, top = 10.dp)
            .padding(8.dp),
        fontFamily = myFont,
        fontSize = 40.sp,
        color = backgroundAccent
    )
}

// entire signup form layout, pass in presenter for TermsSection to show the new page
@Composable
fun SignUpForm(presenter: AuthPresenter) {
    // create a column to house the 3 functions for the user to enter details, terms and conditions and click Done
    Column(
        modifier = Modifier.padding(end = 10.dp, top = 20.dp),
        verticalArrangement = Arrangement.spacedBy((-15).dp)
    ) {
        // set the variables email and password to be edited by the user
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        // set the 2 functions and pass in email and password respectively, and the field = it value to update it as the user writes their info
        EmailInput(email) { email = it }
        PasswordInput(password) { password = it }
        // check the email and password and proceed onto the next page using presenter
        TermsSection(email, password, presenter)
    }
}

// input and label for email, pass in email and a value to change it on demand
@Composable
fun EmailInput(email: String, onEmailChange: (String) -> Unit) {
    Text(
        // create an annotated string to have multiple colors
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontFamily = myFont, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 20.sp)) {
                append("Choose an email to create your account with")
            }
            withStyle(style = SpanStyle(fontFamily = myFont, fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 20.sp)) {
                append("*")
            }
        },
        // set the padding values
        modifier = Modifier.padding(start = 22.dp, top = 2.dp, bottom = 5.dp)
    )

    OutlinedTextField(
        // set the input field value to be email
        value = email,
        singleLine = true, // keep the email only one line.
        onValueChange = { onEmailChange(it) },
        modifier = Modifier.padding(start = 32.dp, 10.dp).padding(bottom = 40.dp),
        // set the label and placeholder values for the text field
        label = { Text(stringResource(R.string.email_string), fontFamily = myFont) },
        placeholder = { Text(stringResource(R.string.example_email), fontFamily = myFont, color = Color.LightGray) },
        textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = myFont),
        // set the colors for the text field to be custom when the text field is focused, unfocused and so on
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = backgroundAccent,
            focusedTextColor = Color.Black
        ),
        shape = CircleShape
    )
}

// input and label for password, pass in password and a value to change it on demand
@Composable
fun PasswordInput(password: String, onPasswordChange: (String) -> Unit) {
    Text(
        // check the annotated string to have multiple colors
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontFamily = myFont, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 20.sp)) {
                append("Choose a password (Must be more than 4 characters long)")
            }
            withStyle(style = SpanStyle(fontFamily = myFont, fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 20.sp)) {
                append("*")
            }
        },
        modifier = Modifier.padding(start = 22.dp, top = 5.dp, bottom = 5.dp)
    )

    OutlinedTextField(
        // set the value to password and update it on demand
        value = password,
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(), // hide the password for privacy sake
        onValueChange = { onPasswordChange(it) },
        modifier = Modifier.padding(start = 32.dp, 10.dp).padding(bottom = 50.dp),
        label = { Text(stringResource(R.string.password_string), fontFamily = myFont) },
        placeholder = { Text(stringResource(R.string.example_password), fontFamily = myFont, color = Color.LightGray) },
        textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = myFont),
        // set the text field colors accordingly
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = backgroundAccent,
            focusedTextColor = Color.Black
        ),
        shape = CircleShape
    )
}

// section with terms, radio buttons, and continue button and goes to the next page using presenter if email and password meets
@Composable
fun TermsSection(email: String, password: String, presenter: AuthPresenter) {
    var selectedOption by remember { mutableStateOf(false) }
    var selectedOption1 by remember { mutableStateOf(false) }
    val backgroundAccentRadio = accentRadioColors() // call the custom radio button features
    val context = LocalContext.current // used for Toast, allows Toast to display itself in the appropriate window
    // set the text values with the right padding, font and size
    Text(
        text = stringResource(R.string.terms_text),
        fontWeight = FontWeight.Bold,
        fontFamily = myFont,
        fontSize = 20.sp,
        modifier = Modifier.padding(start = 22.dp, top = 10.dp, bottom = 3.dp)
    )
    // set a column to house the terms and conditions parts
    Column(
        verticalArrangement = Arrangement.spacedBy((-2).dp)
    ) {
        // terms checkbox
        Row(
            // set alignment and padding values
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(top = 25.dp)
        ) {
            RadioButton(
                // radio button that changes selectedOption on click
                selected = selectedOption,
                onClick = { selectedOption = true },
                // change to custom colors
                colors = backgroundAccentRadio
            )
            // add spacing between button and text
            Spacer(modifier = Modifier.width(8.dp))
            // add the text with the appropriate padding, text, size etc
            Text(
                text = stringResource(R.string.rad_but_1_text),
                modifier = Modifier.padding(bottom = 15.dp, top = 11.dp),
                fontSize = 16.sp,
                fontFamily = myFont
            )
        }

        // disclaimer checkbox
        Row(
            // set the alignment and padding values
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(end = 16.dp)
        ) {
            RadioButton(
                // set selectedOption1 to be true if the value is clicked
                selected = selectedOption1,
                onClick = { selectedOption1 = true },
                // set the custom colors
                colors = backgroundAccentRadio
            )
            // set some spacing between the button and the text
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                // enter the text information and size, font
                text = stringResource(R.string.disclaimer_text),
                fontSize = 16.sp,
                fontFamily = myFont
            )
        }

        // continue button
        Column(
            verticalArrangement = Arrangement.spacedBy((-15).dp),
            modifier = Modifier.padding(top = 30.dp, start = 240.dp)
        ) {
            // boolean value that is only true for the following condition
            val iconAndTextColor = if (email.isNotEmpty() && password.isNotEmpty()) Primary50 else Color.LightGray
            Button(
                onClick = {
                    // checks to see if there are both alphabetical and numerical values
                    val hasLetter = password.any { it.isLetter() }
                    val hasDigit = password.any { it.isDigit() }
                    val isValidPassword = hasLetter && hasDigit
                    if (isValidPassword) {
                        // show the next screen
                        presenter.onSignUpClick(email, password)
                    } else {
                        // send an error message to the user through Toast
                        Toast.makeText(context, "Password must be alphanumeric", Toast.LENGTH_SHORT).show()
                    }
                },
                // make the button visible if radio buttons are pressed, email and pass are not empty
                enabled = selectedOption && selectedOption1 && email.isNotEmpty() && password.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = backgroundAccent),
                // change the shape of the button accordingly
                modifier = Modifier.height(40.dp).width(120.dp),
                shape = RectangleShape
            ) {
                // create a row for the button text and icon
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        // set the text, padding, custom color, size and so on
                        stringResource(id = R.string.continueButtonText),
                        modifier = Modifier.padding(0.dp).padding(end = 15.dp),
                        color = iconAndTextColor,
                        fontSize = 20.sp,
                        fontFamily = myFont
                    )
                    Image(
                        // set the arrow image with proper scaling, and color filter
                        painter = painterResource(R.drawable.sendorizontal),
                        modifier = Modifier.scale(2.5F).padding(top = 5.dp),
                        contentDescription = stringResource(id = R.string.arrow_content_description),
                        colorFilter = ColorFilter.tint(color = iconAndTextColor)
                    )
                }
            }
        }
    }
}