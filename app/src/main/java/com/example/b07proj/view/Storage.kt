package com.example.b07proj.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.b07proj.R
import com.example.b07proj.ui.theme.Primary40
import com.example.b07proj.ui.theme.backgroundAccent

@Composable
fun RenderStoragePage(navController: NavHostController) {
    StoragePage(navController)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoragePage(navController: NavHostController) {

    val buttonItems = listOf(
        Pair("View Your Documents", "documents_screen"),
        Pair("Emergency Contact List", "contacts_screen"),
        Pair("See your Safe Locations", "locations_screen"),
        Pair("View your Medications", "meds_screen")
    )
    LoggedInTopBar(navController) {
        // used to load all tips in a scrollable view (very similar to recycler view)
        LazyColumn(
            modifier = Modifier.padding(5.dp).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                val myFont = FontFamily(Font(R.font.afacad))
                Text(
                    text = stringResource(R.string.StorageHeader),
                    color = backgroundAccent,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = myFont
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(buttonItems) { (buttonText, navigationRoute) ->
                DataButton(
                    navController = navController,
                    navigateTo = navigationRoute,
                    buttonText = buttonText
                )
            }
        }

    }
}

@Composable
fun DataButton(navController: NavHostController, navigateTo: String, buttonText: String) {
    val myFont = FontFamily(Font(R.font.afacad))
    Button(
        onClick = {
            navController.navigate(navigateTo)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
    colors = ButtonDefaults.buttonColors(containerColor = Primary40),
    ) {
        Text(
            text = buttonText,
            fontWeight = FontWeight.Bold,
            fontFamily = myFont,
            fontSize = 20.sp
            )
    }
}