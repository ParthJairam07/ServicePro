package com.example.b07proj.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Support
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.b07proj.R
import com.example.b07proj.ui.theme.BackgroundColor
import com.example.b07proj.ui.theme.Primary40

val myBoldFont = FontFamily(Font(R.font.afacad_bold))


@Composable
fun HomePage(navController: NavHostController) {
    UIHomePage(navController)
}

@Composable
fun UIHomePage(navController: NavHostController) {
    LoggedInTopBar(
        navController
    ){
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ScreenHeaderTop("Welcome Back")


            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            )
            {
                Column {
                    HomePageButton(
                        "View Your Plan",
                        Icons.Outlined.Checklist,
                        navController,
                        "safety_plan_tips"
                    )

                    Spacer(modifier = Modifier.size(32.dp))
                    HomePageButton(
                        "Access Storage",
                        Icons.Outlined.Description,
                        navController,
                        "storagePage"
                    )
                }
                Column {
                    HomePageButton(
                        "Get Local Support",
                        Icons.Outlined.Support,
                        navController,
                        "direct_links"
                    )
                }
            }
        }
    }
}

@Composable
fun HomePageButton(action: String, icon: ImageVector, navController: NavHostController, goTo: String ) {

    Button (
        // Go to the next page
        onClick = {
            navController.navigate(goTo)
        },
        border = BorderStroke(3.dp, Primary40),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary40),
        modifier = Modifier.
        size(width = 120.dp, height = 120.dp),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(16.dp)
    )
    {
        Column (
            modifier = Modifier
                .padding(5.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = action,
                color = Primary40,
                // Center text
                style = TextStyle(
                    lineHeight = 20.sp,
                    fontFamily = myBoldFont,
                    fontSize = 20.sp,
                    fontWeight = FontWeight(300),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .padding(0.dp)
                    .padding(top = 4.dp)
            )
            Icon(
                imageVector = icon,
                contentDescription = "Settings",
                tint = BackgroundColor,
                modifier = Modifier.size(58.dp)
            )
        }
    }
}


@Preview(showBackground = true, name = "Home Page Preview")
@Composable
fun HomePagePreview() {
    HomePage(navController = rememberNavController())
}
