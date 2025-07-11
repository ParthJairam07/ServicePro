package com.example.b07proj.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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

//main login page that allows user to sign up or login
@Composable
fun LoginPage(navController: NavController) {
    UILogin(navController);
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UILogin(navController: NavController) {
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
            Image(
                modifier = Modifier.scale(2.0F).absoluteOffset(68.dp,50.dp),
                painter = painterResource(id = R.drawable.appimage),
                contentDescription = "B07 Project"
            )
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
        }
        Column(
            modifier = Modifier.absoluteOffset(70.dp, 650.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            val myFont = FontFamily(Font(R.font.afacad))
            Button(onClick = { navController.navigate("pin_page") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD3A8)),
                modifier = Modifier.height(80.dp).width(270.dp)
            ) {
                Text("Login Now", color = Color.Black, fontSize = 30.sp, fontFamily = myFont)
            }
            Button(onClick = { navController.navigate("sign_up_page")  },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD3A8)),
                modifier = Modifier.height(80.dp).width(270.dp)
            ) {
                Text("Sign Up", color = Color.Black, fontSize = 30.sp, fontFamily = myFont)
            }
        }
    }
}