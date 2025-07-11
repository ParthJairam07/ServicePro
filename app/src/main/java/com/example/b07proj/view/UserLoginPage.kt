package com.example.b07proj.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

@Composable
fun UserLoginPage(navController: NavController) {
    UIUserLoginPage(navController);
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UIUserLoginPage(navController: NavController) {
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
    )  { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF592C5F))
                .padding(padding)
                .padding(16.dp)
        ) {
            val myFont = FontFamily(Font(R.font.afacad))
            var pin by remember { mutableStateOf("") }
            Text(
                modifier = Modifier
                    .absoluteOffset(100.dp,100.dp)
                    .height(50.dp)
                    .width(200.dp)
                    .size(200.dp)

                ,
                text = "Welcome", fontFamily = myFont,
                fontSize = 50.sp, color = Color.White
            )

            Row (
                modifier = Modifier
                    .absoluteOffset(0.dp, 160.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            )
            {
                Image(
                    modifier = Modifier.scale(0.33F),
                    painter = painterResource(id = R.drawable.pincode),
                    contentDescription = stringResource(id = R.string.contentDescription),
                    colorFilter = ColorFilter.tint(Color(0xFFFFD3A8))
                )
                OutlinedTextField(
                    value = pin,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = { it.also { pin = it } },
                    modifier = Modifier
                        .absoluteOffset((-30).dp, 30.dp),

                    label = { Text("Enter Pin", fontFamily = myFont) },
                    placeholder = { Text("example@gmail.com", fontFamily = myFont) },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = myFont
                    ),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.White, unfocusedBorderColor = Color.hsl(hue = 30f, saturation = 0.8f, lightness = 0.85f), focusedTextColor = Color.White),
                    shape = CircleShape

                )
            }
            Column (
                modifier = Modifier
                    .absoluteOffset(64.dp,125.dp)

            ) {
                Surface(color = Color.Transparent,onClick = { navController.navigate("email_login") }) {
                    Text(
                        "Log in with Email and Password instead.",
                        fontFamily = myFont,
                        style = TextStyle(
                            color = Color(0xFFFFD3A8),
                            fontSize = 18.sp,
                            textDecoration = TextDecoration.Underline
                        )
                    )

                }

            }
            Column (
                modifier = Modifier.absoluteOffset(240.dp, 230.dp)
            ) {
                Button(
                    onClick = { navController.navigate("Start_here")
                        println(pin) },
                    enabled = pin.length >= 4,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA83E92)),
                    modifier = Modifier.height(40.dp).width(120.dp),
                    shape = RectangleShape,

                    ) {
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    )
                    {
                        Text("Done",
                            modifier = Modifier.absoluteOffset(0.dp,(-3).dp).padding(end=15.dp),
                            color = Color(0xFFFFD3A8), fontSize = 20.sp, fontFamily = myFont)
                        Icon(
                            painter = painterResource(R.drawable.sendorizontal),
                            modifier = Modifier.scale(2.5F).padding(top=3.dp),
                            contentDescription = "Arrow Icon",
                            tint = Color(0xFFFFD3A8)
                        )
                    }
                }
            }

        }
    }
}
