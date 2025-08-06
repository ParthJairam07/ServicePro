package com.example.b07proj.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactEmergency
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.b07proj.R
import com.example.b07proj.ui.theme.Primary40
import com.example.b07proj.ui.theme.backgroundAccent

@Composable
fun RenderStoragePage(navController: NavHostController) {
    StoragePage(navController)
}

// small struct for a button triple (with the name, page to nav to, and icon name)
data class ButtonItem(val name: String, val page: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoragePage(navController: NavHostController) {

    val buttonItems = listOf(
        ButtonItem("Documents", "documents_screen", Icons.Filled.FileCopy),
        ButtonItem("Emergency Contacts", "contacts_screen", Icons.Filled.ContactEmergency ),
        ButtonItem("Safe Locations", "locations_screen", Icons.Filled.Map ),
        ButtonItem("Medication", "meds_screen", Icons.Filled.Medication )
    )
    LoggedInTopBar(navController) {

        // For header
        ScreenHeaderTop(stringResource(R.string.StorageHeader))

        // used to load all tips in a scrollable view (very similar to recycler view)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
            }

            items(buttonItems) { (buttonText, navigationRoute, icon) ->
                DataButton(
                    navController = navController,
                    navigateTo = navigationRoute,
                    buttonText = buttonText,
                    icon = icon
                )
            }
            item {
                BackButton(navController)
            }
        }
    }
}

@Composable
fun DataButton(navController: NavHostController, navigateTo: String, buttonText: String, icon: ImageVector ) {
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
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp)
        )

        Text(
            text = buttonText,
            fontWeight = FontWeight.Bold,
            fontFamily = myFont,
            fontSize = 20.sp
        )
    }
}

@Preview( showBackground = true, name = "Storage Preview")
@Composable
fun StoragePagePreview() {
    RenderStoragePage(navController = rememberNavController())
}