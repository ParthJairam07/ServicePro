package com.example.b07proj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.b07proj.model.HandleAuth
import com.example.b07proj.presenter.QuizPresenter
import com.example.b07proj.ui.theme.B07ProjTheme
import com.example.b07proj.view.EmailLogin
import com.example.b07proj.view.LandingPage
import com.example.b07proj.view.LoggedInTopBar
import com.example.b07proj.view.LoginPage
import com.example.b07proj.view.SignUpPage
import com.example.b07proj.view.PinPage
import com.example.b07proj.view.SafetyPlanQuizPage1
import com.example.b07proj.view.RenderTips
import com.example.b07proj.view.SafetyPlanQuizScreen2
import com.example.b07proj.view.outputMap

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            B07ProjTheme {
                //create an instance of the auth
                val auth = HandleAuth()
                val navController = rememberNavController() // to navigate to screens
                //host the navigation graph
                NavHost(
                    navController = navController,
                    startDestination = "loggedintopbar",
                    builder = {
                        //define the route
                        composable("landing_page"){
                            LandingPage(navController, auth)
                        }
                        composable("edit_quiz_screen") {
                            SafetyPlanQuizScreen2( navController, QuizPresenter())
                        }
                        composable("pin_page"){
                            PinPage(navController)
                        }
                        composable("login_page"){
                            LoginPage(navController)
                        }
                        composable("sign_up_page"){
                            SignUpPage(navController)
                        }
                        composable("email_login"){
                            EmailLogin(navController)
                        }
                        composable("loggedintopbar"){
                            LoggedInTopBar(navController)
                        }
                        composable("safety_plan_quiz") {
                            SafetyPlanQuizPage1(navController)
                        }
                        composable("safety_plan_tips") {
                            RenderTips(navController)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    B07ProjTheme {
        Greeting("Android")
    }
}