package com.example.b07proj.view

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.b07proj.R
import com.example.b07proj.model.PinManager
import com.example.b07proj.ui.theme.Primary40
import com.example.b07proj.ui.theme.Primary50
import com.example.b07proj.ui.theme.backgroundAccent

// page for login with pin, takes in a navigation controller to switch to other pages once the tasks have been completed
@Composable
fun PinPage(navController: NavController, nextPage: String) {
    UIPinPage(navController, nextPage)
}

// initialize global variable for font
val myFont = FontFamily(Font(R.font.afacad_regular))
private const val PREFS_FILENAME = "com.example.b07proj.secure_user_prefs"
private const val KEY_ENCRYPTED_USER_DATA = "encrypted_user_data"

// define a custom TextField color scheme to be reused across the app.
private val AppTextInputColors: TextFieldColors
    // set the color of the border when clicked and non-clicked, the label color when clicked and unclicked and so on.
    @Composable
    get() = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Primary50,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.DarkGray,
        unfocusedLabelColor = Primary50,
        focusedLabelColor = Color.White
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UIPinPage(navController: NavController, nextPage: String) {
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
        // create a column to house each of the field
        Column(
            modifier = Modifier
                // create the full background view through a column
                .fillMaxSize()
                .background(color = Primary40)
                .padding(padding)
                .padding(16.dp)
        ) {
            var pin by remember { mutableStateOf("") }
            // call the heading function
            PinHeading()
            // call the inputField and pass in the pin variable and to change it on demand
            PinInputField(pin = pin, onPinChange = { pin = it })
            // call the emailLoginLink and pass in the navController to get to different parts of the UI
            EmailLoginLink(navController)
            // and the continue button
            PinContinueButton(pin = pin, navController = navController, nextPage = nextPage)
        }
    }
}


// heading that says "Enter your Pin"
@Composable
fun PinHeading() {
    Text(
        // create the padding values, keep it one line, and text features
        modifier = Modifier.padding(start = 50.dp, top = 100.dp),
        softWrap = false,
        text = stringResource(R.string.enter_pin),
        fontFamily = myFont,
        fontSize = 50.sp,
        color = Color.White
    )
}

// input field for entering a numeric pin, pass in the pin string and string to update the pin variable as the user enters
@Composable
fun PinInputField(pin: String, onPinChange: (String) -> Unit) {
    // create a row in order to house the text field and the pin code
    Row(
        modifier = Modifier.padding(start = 40.dp, top = 50.dp)
    ) {
        OutlinedTextField(
            // set the value of the text to be stored in the pin variables
            value = pin,
            // ensure that the characters only accounted for are digits and must be of length less than 6
            onValueChange = { newValue ->
                if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                    onPinChange(newValue)
                }
            },
            singleLine = true, // don't allow the enter key to create a new line
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(), // hide the pin for privacy sake
            label = { Text(stringResource(R.string.pin_text), fontFamily = myFont) },
            placeholder = { Text(stringResource(R.string.example_pin), fontFamily = myFont) },
            textStyle = TextStyle(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = myFont
            ),
            colors = AppTextInputColors, // set the colors of the text field to be the custom one initialized above
            shape = CircleShape,
            modifier = Modifier.width(260.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        // icon next to the input box
        Image(
            painter = painterResource(id = R.drawable.pincode),
            contentDescription = stringResource(R.string.pincode_text),
            colorFilter = ColorFilter.tint(color = Primary50),
            modifier = Modifier
                .size(58.dp) // consistent size
                .padding(top = 22.dp, end = 22.dp)
        )
    }
}

// clickable text to login with email instead, pass navController to get to the email login page
@Composable
fun EmailLoginLink(navController: NavController) {
    // create a column for the link to login with email and password
    Column(
        modifier = Modifier.padding(start = 54.dp, top = 5.dp)
    ) {
        // set the text clickable with the Surface() element to navigate to the email_login page
        Surface(color = Color.Transparent, onClick = { navController.navigate("email_login") }) {
            Text(
                stringResource(R.string.email_login),
                fontFamily = myFont,
                style = TextStyle(
                    color = Primary50,
                    fontSize = 20.sp,
                    textDecoration = TextDecoration.Underline // underline to show it's a link
                )
            )
        }
    }
}

// continue button to proceed after entering the pin, pass in the pin to check if it's valid, and navController to get to the next page
@Composable
fun PinContinueButton(pin: String, navController: NavController, nextPage: String) {
    val context = LocalContext.current

    // set a column for the Done button
    Column(
        modifier = Modifier.padding(start = 250.dp, top = 40.dp)
    ) {
        // create a boolean value that keeps the text of the button gray until the valid information is entered
        val iconAndTextColor = if (pin.length >= 4) Primary50 else Color.DarkGray
        // create a button field using the MVP design pattern where a onLoginClick method is being called and the pin are passed in
        Button(
            onClick = {
                val sharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

                //get the encrypted data from the shared preferences
                val encryptedData = sharedPreferences.getString(KEY_ENCRYPTED_USER_DATA, null)

                if (encryptedData == null) {
                    Log.e("PinLogin", "No PIN data found on device.")
                    return@Button
                }
                // 3. Decrypt and get the stored PIN
                val storedPin = PinManager.getPin(encryptedData)
                if (storedPin == pin) {
                    // SUCCESS!
                    val userUUID = PinManager.getUuid(encryptedData)
                    Log.d("PinLogin", "PIN Correct! Logging in user: $userUUID")
                    // You can now pass the userUUID to your home screen or store it in a ViewModel

                    navController.navigate(nextPage) // Navigate to your main app screen
                } else {
                    // FAILURE!
                    Log.w("PinLogin", "Incorrect PIN entered.")
                }
            },
            enabled = pin.length >= 4, // only enable the button if the fields are non-empty
            colors = ButtonDefaults.buttonColors(containerColor = backgroundAccent),
            modifier = Modifier.height(45.dp).width(120.dp),
            shape = RectangleShape,
        ) {
            // set a new row to house the Done text and arrow icon
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // enter the text for the Done button and set the padding values, and custom colors
                Text(
                    stringResource(id = R.string.continueButtonText),
                    modifier = Modifier.padding(0.dp, 0.dp, bottom = 5.dp).padding(end = 15.dp),
                    color = iconAndTextColor,
                    fontSize = 20.sp,
                    fontFamily = myFont
                )
                // set the arrow image for the Done button and tint it accordingly
                Image(
                    painter = painterResource(R.drawable.sendorizontal),
                    modifier = Modifier.scale(2.5F).padding(top = 5.dp),
                    contentDescription = stringResource(id = R.string.arrow_content_description),
                    colorFilter = ColorFilter.tint(color = iconAndTextColor)
                )
            }
        }
    }
}