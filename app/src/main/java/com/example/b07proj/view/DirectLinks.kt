package com.example.b07proj.view

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.b07proj.presenter.DirectLinksPresenter

// The data structure for each resource (that's the link/info for a local service to help the user)
data class Resource(
    val name: String?,
    val category: String?,
    val phone: String?,
    val url: String?
)

// The UI / viewer for the direct links page
@Composable
fun DirectLinks(navController: NavHostController) {
    // The presenter for the page
    val presenter = remember { DirectLinksPresenter() }

    // Required for loading circle
    val isLoading by presenter.loading.collectAsState()
    // Holds error message if there is one
    val errorMessage by presenter.errorMessage.collectAsState()

    // Holds the city and resources for the page to show
    val city by presenter.warmupCity.collectAsState()
    val resources by presenter.resources.collectAsState()

    // Fetches the city and resources from the presenter
    LaunchedEffect(Unit) {
        presenter.fetchCityAndResources(navController.context)
    }

    // Actual UI to display the above info
    LoggedInTopBar(
        navController = navController
    ) {
        // Show the title
        ScreenHeaderTop(
            text = if (city == null) "Support Services" else "Support Services in $city"
        )

        // Checks for isLoading, errorMessage to be true
        // If they are, displays a loading indicator or error message
        // Otherwise, displays list of resources
        when {
            // If loading, shows loading indicator
            isLoading -> Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) { CircularProgressIndicator() }
            // If error, shows error message
            errorMessage != null -> Text("Error: $errorMessage")
            // Otherwise there are no issues so we show the actual content
            else -> {
                // Column for all resources
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Iterate through each resource and display it
                    resources.forEach { res ->
                        ResourceComposable(res, navController)
                    }
                    BackButton(navController)
                }
            }
        }
    }
}

@Composable
fun ResourceComposable(res: Resource, navController: NavHostController) {
    if (res.name == null) return // assert name is not null via guard clause

    // Column holds title, category, and phone number
    Column(Modifier.fillMaxWidth()) {
        Text(
            res.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                res.url?.let { navController.context.startActivity(Intent(Intent.ACTION_VIEW, it.toUri())) }
            },
            fontFamily = myFont,
            // Make underlined
            textDecoration = TextDecoration.Underline
        )
        // For category and phone number, indented for readability
        Column(
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Row {
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = res.category.toString(),
                    fontWeight = FontWeight(400),
                    fontFamily = myBoldFont
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.Phone, // Use the imported Phone icon
                    contentDescription = "Phone Icon", // Add content description for accessibility
                    modifier = Modifier.scale(0.66f)
                )
                Text(
                    "${res.phone}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = myFont
                )
            }
        }
    }
}

// Wont work as Preview doesn't work with FireBase
@Preview(showBackground = true, name = "Real JSON Preview")
@Composable
fun DirectLinksPagePreview() {
    DirectLinks(navController = rememberNavController())  // Now this will actually load your res/raw/services.json
}