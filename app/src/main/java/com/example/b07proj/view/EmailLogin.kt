package com.example.b07proj.view

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.b07proj.R
import com.example.b07proj.model.HandleAuth
import com.example.b07proj.presenter.AuthPresenter
import com.example.b07proj.ui.theme.Primary40
import com.example.b07proj.ui.theme.Primary50
import com.example.b07proj.ui.theme.backgroundAccent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Mail


// Renders for the email login page
@Composable
fun EmailLogin(navController: NavHostController, nextPage: String) {
    // Call the UIEmailLogin() function which loads the full UI onto the app
    UIEmailLogin(navController, nextPage)
}

// Define a custom TextField color scheme to be reused across the app.
private val AppTextInputColors: TextFieldColors
    @Composable
    get() = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Primary50,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.DarkGray,
        unfocusedLabelColor = Primary50,
        focusedLabelColor = Color.White
    )

// Create a function for the UI of the login
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UIEmailLogin(navController: NavHostController, nextPage: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val presenter = remember {
        AuthPresenter(
            view = object : SignUpView {
                override fun onSignUpSuccess() {
                    // Navigate and clear the back stack so user can't go back to login
                    navController.navigate(nextPage) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }

                override fun showError(message: String) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Backend Logic: Launcher for Google Sign-In
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                val idToken = account.idToken!!
                presenter.onGoogleSignInSucceeded(idToken)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(context, "Google Sign-In failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Image(
                        painter = painterResource(R.drawable.templogo),
                        contentDescription = stringResource(id = R.string.logoDescription),
                    )
                }
            )
        },
        floatingActionButton = {
            ExitButton()
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Primary40)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val myFont = FontFamily(Font(R.font.afacad))

            LoginTitleText(myFont)

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                EmailInputField(email, onEmailChange = { email = it }, myFont)
                PasswordInputField(password, onPasswordChange = { password = it }, myFont)
                LoginButton(email, password, presenter, context, myFont)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "OR",
                color = Color.White.copy(alpha = 0.7f),
                fontFamily = myFont,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Google Sign-In Button with Backend Logic
            GoogleSignInButton(
                onClick = {
                    coroutineScope.launch {
                        // Configure Google Sign-In
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(context.getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                        // Launch the sign-in UI
                        launcher.launch(googleSignInClient.signInIntent)
                    }
                },
                modifier = Modifier.padding(horizontal = 30.dp)
            )
        }
    }
}


@Composable
fun GoogleSignInButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google_logo),
                contentDescription = "Google sign-in button"
            )
            Text(
                text = "Sign in with Google",
                modifier = Modifier.padding(start = 12.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LoginTitleText(myFont: FontFamily) {
    Text(
        text = stringResource(id = R.string.log_in),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 100.dp, bottom = 20.dp),
        fontFamily = myFont,
        fontSize = 50.sp,
        color = Color.White,
    )
}

@Composable
fun EmailInputField(email: String, onEmailChange: (String) -> Unit, myFont: FontFamily) {
    Row(
        modifier = Modifier.padding(horizontal = 30.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = email,
            singleLine = true,
            onValueChange = onEmailChange,
            modifier = Modifier.weight(1f),
            label = { Text(stringResource(id = R.string.email_string), fontFamily = myFont) },
            placeholder = { Text(stringResource(id = R.string.example_email), fontFamily = myFont) },
            textStyle = TextStyle(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = myFont
            ),
            colors = AppTextInputColors,
            shape = CircleShape
        )
        Icon(
            imageVector = Icons.Default.Mail,
            contentDescription = stringResource(id = R.string.mail_icon),
            tint = Primary50,
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
fun PasswordInputField(password: String, onPasswordChange: (String) -> Unit, myFont: FontFamily) {
    Row(
        modifier = Modifier.padding(horizontal = 30.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = password,
            singleLine = true,
            onValueChange = onPasswordChange,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.weight(1f),
            label = { Text(stringResource(id = R.string.password_string), fontFamily = myFont) },
            placeholder = { Text(stringResource(id = R.string.example_password), fontFamily = myFont) },
            textStyle = TextStyle(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = myFont
            ),
            colors = AppTextInputColors,
            shape = CircleShape
        )
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = stringResource(id = R.string.password_icon),
            tint = Primary50,
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
fun LoginButton(email: String, password: String, presenter: AuthPresenter, context: android.content.Context, myFont: FontFamily) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.End
    ) {
        val iconAndTextColor = if (email.isNotEmpty() && password.isNotEmpty()) Primary50 else Color.DarkGray
        Button(
            onClick = {
                val hasLetter = password.any { it.isLetter() }
                val hasDigit = password.any { it.isDigit() }
                val isValidPassword = hasLetter && hasDigit
                if (isValidPassword) {
                    presenter.onLoginClick(email, password)
                } else {
                    Toast.makeText(context, "Password must be alphanumeric", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = email.isNotEmpty() && password.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(containerColor = backgroundAccent),
            modifier = Modifier
                .height(45.dp)
                .width(120.dp),
            shape = RectangleShape,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(id = R.string.continueButtonText),
                    modifier = Modifier.padding(end = 15.dp),
                    color = iconAndTextColor,
                    fontSize = 20.sp,
                    fontFamily = myFont
                )
                Image(
                    painter = painterResource(R.drawable.sendorizontal),
                    modifier = Modifier
                        .scale(2.5F)
                        .padding(top = 2.dp),
                    contentDescription = stringResource(id = R.string.arrow_content_description),
                    colorFilter = ColorFilter.tint(color = iconAndTextColor)
                )
            }
        }
    }
}

@Preview
@Composable
fun EmailLoginPreview() {
    EmailLogin(navController = NavHostController(LocalContext.current), nextPage = "home")
}