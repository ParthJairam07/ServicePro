package com.example.b07proj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.b07proj.model.HandleAuth
import com.example.b07proj.ui.theme.B07ProjTheme
import com.example.b07proj.view.EmailLogin
import com.example.b07proj.view.HomePage
import com.example.b07proj.view.LandingPage
import com.example.b07proj.view.LoggedInTopBar
import com.example.b07proj.view.LoginPage
import com.example.b07proj.view.SignUpPage
import com.example.b07proj.view.PinPage
import com.example.b07proj.view.RenderAddContactsPage
import com.example.b07proj.view.RenderAddDocumentsPage
import com.example.b07proj.view.RenderAddMedicationPage
import com.example.b07proj.view.RenderAddSafeLocationsPage
import com.example.b07proj.view.RenderDocumentPage
import com.example.b07proj.view.RenderEmergencyContactPage
import com.example.b07proj.view.RenderMedicationPage
import com.example.b07proj.view.RenderSafeLocationsPage
import com.example.b07proj.view.SafetyPlanQuizPage1
import com.example.b07proj.view.RenderTips
import com.example.b07proj.view.SafetyPlanQuizPage2
import com.example.b07proj.view.SafetyPlanQuizPage3
import com.example.b07proj.view.RenderStoragePage
import com.example.b07proj.view.CreatePin
import com.example.b07proj.view.DialogBox
import com.example.b07proj.view.DirectLinks
import com.example.b07proj.view.EditParsable
import com.example.b07proj.view.EditQuizAnswers
import kotlin.Boolean

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            B07ProjTheme {
                //create an instance of the auth
                val navController = rememberNavController() // to navigate to screens
                //host the navigation graph
                NavHost(
                    navController = navController,
                    startDestination = "email_login",
                    builder = {
                        //define the route
                        composable("landing_page") {
                            LandingPage(navController)
                        }
                        composable("create_pin") {
                            CreatePin(navController)
                        }

                        composable("edit_quiz_screen") {
                            EditQuizAnswers(navController)
                        }
                        composable("pin_page") {
                            PinPage(navController)
                        }
                        composable("login_page") {
                            LoginPage(navController)
                        }
                        composable("sign_up_page") {
                            SignUpPage(navController)
                        }
                        composable("email_login") {
                            EmailLogin(navController)
                        }
                        composable("loggedintopbar") {
                            LoggedInTopBar(navController) {}
                        }
                        composable("safety_plan_quiz") {
                            SafetyPlanQuizPage1(navController)
                        }
                        composable("safety_plan_tips") {
                            RenderTips(navController)
                        }
                        composable("safetyPlanQuizPage2") {
                            SafetyPlanQuizPage2(navController)
                        }
                        composable("safetyPlanQuizPage3") {
                            SafetyPlanQuizPage3(navController)
                        }
                        composable("storagePage") {
                            RenderStoragePage(navController)
                        }
                        composable("documents_screen") {
                            RenderDocumentPage(navController)
                        }
                        composable("contacts_screen") {
                            RenderEmergencyContactPage(navController)
                        }
                        composable("locations_screen") {
                            RenderSafeLocationsPage(navController)
                        }
                        composable("meds_screen") {
                            RenderMedicationPage(navController)
                        }
                        composable("add_documents") {
                            RenderAddDocumentsPage(navController)
                        }
                        composable("home_page") {
                            HomePage(navController)
                        }
                        composable("direct_links") {
                            DirectLinks()
                        }
                        // this page is for adding new contacts or editing ones, we need
                        // to pass in optional argument contactId to edit
                        composable(
                            "add_or_edit_contacts?dataItemId={dataItemId}",
                            arguments = listOf(
                                navArgument("dataItemId") {
                                    type = NavType.StringType
                                    nullable = true
                                }
                            )
                        ) { backStackEntry ->
                            RenderAddContactsPage(navController)
                        }

                        // this page is for adding new contacts or editing ones, we need
                        // to pass in optional argument contactId to edit
                        composable(
                            "add_or_edit_safe_locations?dataItemId={dataItemId}",
                            arguments = listOf(
                                navArgument("dataItemId") {
                                    type = NavType.StringType
                                    nullable = true
                                }
                            )
                        ) { backStackEntry ->
                            RenderAddSafeLocationsPage(navController)
                        }

                        composable(
                            "add_or_edit_medications?dataItemId={dataItemId}",
                            arguments = listOf(
                                navArgument("dataItemId") {
                                    type = NavType.StringType
                                    nullable = true
                                }
                            )
                        ) { backStackEntry ->
                            RenderAddMedicationPage(navController)
                        }

                    }
                )
            }
        }
    }
}
