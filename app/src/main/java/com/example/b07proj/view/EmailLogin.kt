package com.example.b07proj.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.b07proj.R
import com.example.b07proj.model.HandleAuth
import com.example.b07proj.presenter.AuthPresenter

@Composable
fun EmailLogin(navController: NavHostController) {
    UIEmailLogin(navController);
}
private val AppTextInputColors: TextFieldColors
    @Composable
    get() = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.White, unfocusedBorderColor = Color.hsl(hue = 30f, saturation = 0.8f, lightness = 0.85f),
        focusedTextColor = Color.White, unfocusedTextColor = Color.DarkGray,
        unfocusedLabelColor = Color.hsl(hue = 30f, saturation = 0.8f, lightness = 0.85f), focusedLabelColor = Color.White)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UIEmailLogin(navController: NavHostController) {
    val context = LocalContext.current

    val presenter = remember {
        AuthPresenter(
            auth = HandleAuth(),
            view = object : SignUpView {
                override fun onSignUpSuccess() {
                    navController.navigate("landing_page")
                }

                override fun showError(message: String) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

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
                .background(Color(0xFF592C5F))
                .padding(padding)
                .padding(16.dp)
        ) {
            val myFont = FontFamily(Font(R.font.afacad))
            Text(
                text = "Log In",
                modifier = Modifier
                    .absoluteOffset(100.dp, 100.dp)
                    .padding(8.dp), // add spacing if needed
                fontFamily = myFont,
                fontSize = 50.sp,
                color = Color.White
            )
        }
        Column(
            modifier = Modifier.absoluteOffset(30.dp, 200.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val myFont = FontFamily(Font(R.font.afacad))
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            Row(
                modifier = Modifier.absoluteOffset(30.dp, 100.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)

            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { it.also { email = it } },
                    modifier = Modifier
                        .absoluteOffset((-30).dp, 0.dp),

                    label = { Text("Email", fontFamily = myFont) },
                    placeholder = { Text("example@gmail.com", fontFamily = myFont) },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = myFont
                    ),
                    colors = AppTextInputColors,
                    shape = CircleShape

                )
                Image(
                    modifier = Modifier.absoluteOffset((-20).dp, 24.dp),
                    painter = painterResource(R.drawable.baseline_mail_24),
                    contentDescription = stringResource(id = R.string.contentDescription),
                    colorFilter = ColorFilter.tint(Color(0xFFFFD3A8))
                )
            }
            Row(
                modifier = Modifier.absoluteOffset(30.dp, 90.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)

            ) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { it.also { password = it } },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .absoluteOffset((-30).dp, 30.dp),

                    label = { Text("Password", fontFamily = myFont) },
                    placeholder = { Text("CandyPants764!", fontFamily = myFont) },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = myFont
                    ),
                    colors = AppTextInputColors,
                    shape = CircleShape

                )
                Image(
                    modifier = Modifier.absoluteOffset((-72).dp, 1.dp).scale(0.27F),
                    painter = painterResource(R.drawable.passwordicon),
                    contentDescription = "password icon",
                    colorFilter = ColorFilter.tint(Color(0xFFFFD3A8))
                )
            }
            Column(
                modifier = Modifier.absoluteOffset(220.dp, 140.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                )
                {
                    Button(
                        onClick = {
                            presenter.onLoginClick(email, password)
                        },
                        enabled = !email.isEmpty() && !password.isEmpty(),
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