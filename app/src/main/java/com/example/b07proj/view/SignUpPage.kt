package com.example.b07proj.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
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
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.b07proj.R

//page for user to sign up
@Composable
fun SignUpPage(navController: NavHostController) {
    UISignUpPage(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UISignUpPage(navController: NavHostController) {
    Scaffold(

        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Logo here!")

                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(16.dp)
        ) {
            val myFont = FontFamily(Font(R.font.afacad))
            Text(
                text = "Let's get you set up",
                modifier = Modifier
                    .padding(start = 30.dp, top = 30.dp)
                    .padding(8.dp), // add spacing if needed
                fontFamily = myFont,
                fontSize = 40.sp,
                color = Color(0xFFA83E92)
            )
        }
        Column (
            modifier = Modifier.absoluteOffset((-10).dp, 200.dp),
            verticalArrangement = Arrangement.spacedBy((-15).dp)

        ) {
            val myFont = FontFamily(Font(R.font.afacad))
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontFamily = myFont, fontWeight = FontWeight.Bold, color = Color.Black, fontSize=20.sp)) {
                        append("Choose an email to create your account with")
                    }
                    withStyle(style = SpanStyle(fontFamily = myFont, fontWeight = FontWeight.Bold, color = Color.Red, fontSize=20.sp)) {
                        append("*")
                    }
                },
                modifier = Modifier
                    .padding(start = 22.dp, top = 5.dp, bottom = 5.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { it.also { email = it } },
                modifier = Modifier
                    .absoluteOffset(32.dp, 10.dp)
                    .padding(bottom=40.dp),
                label = { Text("Email", fontFamily = myFont) },
                placeholder = { Text("example1@gmail.com", fontFamily = myFont) },
                textStyle = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = myFont
                ),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.White, unfocusedBorderColor = Color.hsl(hue = 30f, saturation = 0.8f, lightness = 0.85f), focusedTextColor = Color.White),
                shape = CircleShape

            )
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontFamily = myFont, fontWeight = FontWeight.Bold, color = Color.Black, fontSize=20.sp)) {
                        append("Choose a password (Must be more than 4 characters long)")
                    }
                    withStyle(style = SpanStyle(fontFamily = myFont, fontWeight = FontWeight.Bold, color = Color.Red, fontSize=20.sp)) {
                        append("*")
                    }
                },
                modifier = Modifier
                    .padding(start = 22.dp, top = 5.dp, bottom = 5.dp)
            )
            OutlinedTextField(
                value = password,
                visualTransformation = PasswordVisualTransformation(),
                onValueChange = { it.also { password = it } },
                modifier = Modifier
                    .absoluteOffset(32.dp, 10.dp)
                    .padding(bottom=50.dp),
                label = { Text("Password", fontFamily = myFont) },
                placeholder = { Text("CandyPants764!", fontFamily = myFont) },
                textStyle = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = myFont
                ),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.White, unfocusedBorderColor = Color.hsl(hue = 30f, saturation = 0.8f, lightness = 0.85f), focusedTextColor = Color.White),
                shape = CircleShape

            )
            Text(
                text = "Check the boxes below to agree with the following:",
                fontWeight = FontWeight.Bold,
                fontFamily = myFont,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(start = 22.dp, top = 15.dp, bottom = 7.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy((-2).dp)
            ) {
                var selectedOption by remember { mutableStateOf(false) }
                var selectedOption1 by remember { mutableStateOf(false) }

                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(top = 25.dp)
                ) {
                    RadioButton(
                        selected = selectedOption,
                        onClick = { selectedOption = true },
                        enabled = !selectedOption
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text="I agree to the terms and services (if there is one >~<)",
                        modifier = Modifier
                            .clickable(enabled = !selectedOption) { selectedOption = true }
                            .padding(bottom = 15.dp, top = 11.dp),
                        fontSize = 16.sp,
                        fontFamily= myFont
                    )
                }
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    RadioButton(
                        selected = selectedOption1,
                        onClick = { selectedOption1 = true },
                        enabled = !selectedOption1
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I understand this application is not a substitute for emergency services and that safety plans are personal and not guaranteed to prevent harm.",
                        modifier = Modifier
                            .clickable(enabled = !selectedOption1) { selectedOption1 = true },
                        fontSize = 16.sp,
                        fontFamily= myFont
                    )
                }
                Column (
                    verticalArrangement = Arrangement.spacedBy((-15).dp),
                    modifier = Modifier.padding(top = 30.dp, start = 240.dp)
                ) {
                    Button(
                        onClick = {
                            navController.navigate("create_pin")
                            println(selectedOption)
                            println(selectedOption1)
                        },
                        enabled = selectedOption && selectedOption1 && email.isNotEmpty() && password.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA83E92)),
                        modifier = Modifier.height(40.dp).width(120.dp),
                        shape = RectangleShape,

                        ) {
                        Text(
                            "Done",
                            modifier = Modifier.absoluteOffset(0.dp, 0.dp).padding(end=15.dp),
                            color = Color(0xFFFFD3A8), fontSize = 20.sp, fontFamily = myFont
                        )
                        Image(
                            painter = painterResource(R.drawable.sendorizontal),
                            modifier = Modifier.scale(2.5F),
                            contentDescription = stringResource(id = R.string.contentDescription3),
                            colorFilter = ColorFilter.tint(Color(0xFFFFD3A8))

                        )
                    }
                }

            }


        }
    }
}