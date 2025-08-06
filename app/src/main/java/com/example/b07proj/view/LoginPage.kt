package com.example.b07proj.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b07proj.R
import com.example.b07proj.ui.theme.Primary40
// main login page that allows user to sign up or login
@Composable
fun LoginPage(navController: NavController) {
    // call the UI layer for the login
    UILogin(navController)
}

// main UI function that uses Scaffold to organize the layout
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UILogin(navController: NavController) {
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
        Column(
            modifier = Modifier
                // enter the appropriate elements, such as filling to the entire page, color and padding values
                .fillMaxSize()
                .background(color = Primary40)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // render the app title image and welcome text
            LoginTitle()
            // render the login and signup buttons
            LoginOptions(navController)
        }

    }
}


// create a function called LoginTitle for B07Proj image + welcome title
@Composable
fun LoginTitle() {
    // set the fonts used for the UI in a variable
    val myFont = FontFamily(Font(R.font.afacad))
    // set the title page text using the Text() element
    Text(
        modifier = Modifier.padding(top = 35.dp),
        // create the text, font, size and color
        text = stringResource(R.string.welcome_text),
        fontFamily = myFont,
        fontSize = 70.sp,
        color = Color.White
    )

    // create the title for the page through the Text() element and project image
    Image(
        // set the appropriate UI location, the image and description
        modifier = Modifier.scale(1.8F).padding(top = 75.dp),
        painter = painterResource(id = R.drawable.optionpageimage),
        contentDescription = stringResource(id = R.string.title_image),
    )
}

// button section for login or signup, this takes in a navController in order to send the user to the appropriate page
@Composable
fun LoginOptions(navController: NavController) {
    // create a column to incorporate the buttons
    Column(
        // create the padding and the vertical arrangement for the columns
        modifier = Modifier.padding(top = 130.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // set the font value
        val myFont = FontFamily(Font(R.font.afacad))


        // create a button to navigate to the pin page if the user already has an account
        Button(
            onClick = { navController.navigate("pin_page") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD3A8)),
            modifier = Modifier.height(80.dp).width(270.dp)
        ) {
            Text(
                stringResource(R.string.login_text),
                color = Color.Black,
                fontSize = 30.sp,
                fontFamily = myFont
            )
        }

        // create a button to navigate to the create account page if the user does not have an account
        Button(
            onClick = { navController.navigate("sign_up_page") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD3A8)),
            modifier = Modifier.height(80.dp).width(270.dp)
        ) {
            Text(
                stringResource(id = R.string.signup_text),
                color = Color.Black,
                fontSize = 30.sp,
                fontFamily = myFont
            )
        }
    }
}