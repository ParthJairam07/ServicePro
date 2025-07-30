package com.example.b07proj.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.b07proj.R
import com.example.b07proj.model.HandleAuth
import com.example.b07proj.presenter.AuthPresenter
import com.example.b07proj.ui.theme.Primary40
import com.example.b07proj.ui.theme.Primary50
import com.example.b07proj.ui.theme.backgroundAccent

// renders for the email login page
@Composable
fun EmailLogin(navController: NavHostController, nextPage: String) {
    // call the UIEmailLogin() function which loads the full UI onto the app
    UIEmailLogin(navController, nextPage)
}

// define a custom TextField color scheme to be reused across the app.
private val AppTextInputColors: TextFieldColors
    // set the color of the border when clicked and non-clicked, the label color when clicked and unclicked and so on.
    @Composable
    get() = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.White, // when the field is selected
        unfocusedBorderColor = Primary50, // when the field is not selected
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.DarkGray,
        unfocusedLabelColor = Primary50,
        focusedLabelColor = Color.White
    )

// create a function for the UI of the login
@OptIn(ExperimentalMaterial3Api::class) // allow usage of experimental Material3 APIs like Scaffold
@Composable
fun UIEmailLogin(navController: NavHostController, nextPage: String) {
    // get the current context of the app to use for things like Toasts
    val context = LocalContext.current

    // set a presenter variable that stores whether or not the auth was successful
    val presenter = remember {
        // use MVP pattern: presenter handles login logic and updates the view
        AuthPresenter(
            // check the auth through HandleAuth() function
            auth = HandleAuth(),
            view = object : SignUpView {
                // override function onSignUpSuccess to navigate to landing_page once successful
                override fun onSignUpSuccess() {
                    navController.navigate(nextPage)
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
        },
        floatingActionButton = {
            ExitButton(
                modifier = Modifier
                    .padding(5.dp)    // placement
            )
        }
    ) { padding ->
        // create the full background view through a column
        Column(
            modifier = Modifier
                // enter the appropriate elements, such as filling to the entire page, color and padding values
                .fillMaxSize()
                .background(color = Primary40)
                .padding(padding)
                .padding(16.dp)
        ) {
            // set the fonts used for the UI in a variable
            val myFont = FontFamily(Font(R.font.afacad))

            // Create the title for the page through the Text() element
            LoginTitleText(myFont)

            // create another column for the information to be entered by the user to login
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                // set the read-only variables of email, password and myFont
                var email by remember { mutableStateOf("") } // stores the email input
                var password by remember { mutableStateOf("") } // stores the password input
                // create a new row to house the email field and the mail icon
                EmailInputField(email, onEmailChange = { email = it }, myFont)
                // set a new row element for the password field and icon
                PasswordInputField(password, onPasswordChange = { password = it }, myFont)
                // set a column for the button icon and perform validation before calling presenter
                LoginButton(email, password, presenter, context, myFont)
            }
        }
    }
}

// create the title for the page through the Text() element
@Composable
fun LoginTitleText(myFont: FontFamily) {
    Text(
        text = stringResource(id = R.string.log_in),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(115.dp, top = 100.dp),
        // set font, size and color of the title
        fontFamily = myFont,
        fontSize = 50.sp,
        color = Color.White,
    )
}

// create a text field row for email input, taking in an email string to store the users values, onEmailChange to update the email and font
@Composable
fun EmailInputField(email: String, onEmailChange: (String) -> Unit, myFont: FontFamily) {
    Row(
        modifier = Modifier.padding(30.dp, top = 40.dp),
        // set the spacing between the icon and the text field
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        // create a text field through the OutlinedTextField() elements
        OutlinedTextField(
            // set the value to email variable to store what the user enters, and update it through OnValueChange
            value = email,
            singleLine = true,
            onValueChange = onEmailChange,
            modifier = Modifier.padding(0.dp, 0.dp),
            // set the text for the field, and the hint through label and placeholder
            label = { Text(stringResource(id = R.string.email_string), fontFamily = myFont) },
            placeholder = { Text(stringResource(id = R.string.example_email), fontFamily = myFont) },
            textStyle = TextStyle(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = myFont
            ),
            // follow the styles from the variable above for the text field outline
            colors = AppTextInputColors,
            shape = CircleShape
        )
        Image(
            modifier = Modifier.padding(start = 22.dp, 50.dp).scale(2.6F),
            painter = painterResource(R.drawable.baseline_mail_24),
            contentDescription = stringResource(id = R.string.mail_icon),
            colorFilter = ColorFilter.tint(color = Primary50)
        )
    }
}

// create a text field row for password input, that takes in a password of type string, a string to keep track of what is being updated, and myFont
@Composable
fun PasswordInputField(password: String, onPasswordChange: (String) -> Unit, myFont: FontFamily) {
    Row(
        // set the padding values on the UI and the spacing between the field and icon
        modifier = Modifier.padding(30.dp, top = 5.dp),
    ) {
        // create a text field through the OutlinedTextField() parameter
        OutlinedTextField(
            // set the value to password to store user value
            value = password,
            singleLine = true,
            // change the password variables as the user enters their information in realtime
            onValueChange = onPasswordChange,
            // hide the password to ensure screen security when entering it
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.padding((0).dp, top = 10.dp),
            // set the text field label to password and a hint through placeholder
            label = { Text(stringResource(id = R.string.password_string), fontFamily = myFont) },
            placeholder = { Text(stringResource(id = R.string.example_password), fontFamily = myFont) },
            textStyle = TextStyle(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = myFont
            ),
            // set the default text field color parameters initialized above
            colors = AppTextInputColors,
            shape = CircleShape
        )
        // set the password icon (lock image)
        Image(
            modifier = Modifier.padding(start = 0.dp, top = 19.dp, end = 21.dp).scale(0.7F),
            painter = painterResource(R.drawable.passwordicon),
            contentDescription = stringResource(id = R.string.password_icon),
            colorFilter = ColorFilter.tint(color = Primary50)
        )
    }
}

// create a login button that calls the presenter method only if password is valid, taking in the email, password, presenter, context for the Toast, and font
@Composable
fun LoginButton(
    email: String,
    password: String,
    presenter: AuthPresenter,
    context: android.content.Context,
    myFont: FontFamily
) {
    Column(
        // set a column for the button icon
        modifier = Modifier.padding(start = 250.dp, 30.dp)
    ) {
        // create a boolean value that keeps the text of the button gray until the valid information is entered
        val iconAndTextColor = if (email.isNotEmpty() && password.isNotEmpty()) Primary50 else Color.DarkGray

        // create a button field using the MVP design pattern where a onLoginClick method is being called and the email and password are passed in
        Button(
            onClick = {
                // check whether the password contains letters and digits
                val hasLetter = password.any { it.isLetter() }
                val hasDigit = password.any { it.isDigit() }
                val isValidPassword = hasLetter && hasDigit
                if (isValidPassword) {
                    // call the presenter method to authenticate the user and tells HandleAuth() to log in
                    presenter.onLoginClick(email, password)
                } else {
                    // create a toast where if the password does not contain both alphabetical and numerical values, then the user is forced to retry
                    Toast.makeText(context, "Password must be alphanumeric", Toast.LENGTH_SHORT).show() // take the context value to know which window to display
                }
            },
            // only enable the button if the fields are non-empty
            enabled = email.isNotEmpty() && password.isNotEmpty(),
            // set the colors of the button to a specific set of custom ones
            colors = ButtonDefaults.buttonColors(containerColor = backgroundAccent),
            modifier = Modifier.height(45.dp).width(120.dp),
            shape = RectangleShape,
        ) {
            // set a new row to house the Done text and arrow icon
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    stringResource(id = R.string.continueButtonText),
                    modifier = Modifier.padding(0.dp, 0.dp, bottom = 5.dp).padding(end = 15.dp),
                    color = iconAndTextColor,
                    fontSize = 20.sp,
                    fontFamily = myFont
                )
                Image(
                    // set the image, description and color value through the painter, contentDescription and colorFilter fields
                    painter = painterResource(R.drawable.sendorizontal),
                    modifier = Modifier.scale(2.5F).padding(top = 5.dp),
                    contentDescription = stringResource(id = R.string.arrow_content_description),
                    colorFilter = ColorFilter.tint(color = iconAndTextColor)
                )
            }
        }
    }
}