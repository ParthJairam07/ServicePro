package com.example.b07proj.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.b07proj.R
import com.example.b07proj.model.dataCategories.SafeLocation
import com.example.b07proj.presenter.dataItems.Categories
import com.example.b07proj.presenter.dataItems.ViewDataItemContract
import com.example.b07proj.presenter.dataItems.ViewContactsPresenter
import com.example.b07proj.ui.theme.backgroundAccent

@Composable
fun RenderSafeLocationsPage(navController: NavHostController) {
    SafeLocationPage(navController)
}

@Composable
fun SafeLocationPage(navController: NavHostController) {
    // To determine if we show the spinner or not when fetching/storing data
    var isLoading by remember { mutableStateOf(true) }
    // the list of locations to display
    var locations by remember { mutableStateOf<List<SafeLocation>>(emptyList()) }
    // any error we got when fetching data
    var errorMessage by remember { mutableStateOf<String?>(null) }
    // if empty contact list from users
    var showEmptyState by remember { mutableStateOf(false) }

    // set up presenter
    val presenter = remember { ViewContactsPresenter<SafeLocation>(null) }
    // implement contract for view
    val view = remember {
        object : ViewDataItemContract.View<SafeLocation> {
            override fun showLoading() {
                isLoading = true
            }

            override fun hideLoading() {
                isLoading = false
            }

            override fun displayContacts(fetchedContacts: List<SafeLocation>) {
                locations = fetchedContacts
                showEmptyState = false
                errorMessage = null
            }

            override fun displayEmptyState() {
                showEmptyState = true
                errorMessage = null
            }

            override fun displayError(message: String) {
                errorMessage = message
                showEmptyState = false
            }

            override fun onDataItemDeleted(dataItemId: String) {
                // when we deleted a contact, remove that contact off the list and update
                val updatedList = locations.filterNot { it.id == dataItemId }
                locations = updatedList
                if (updatedList.isEmpty()) {
                    showEmptyState = true
                }
            }
        }

    }
    // now we can connect the view to the presenter
    DisposableEffect(presenter) {
        presenter.view = view
        // Load data when the view is ready
        presenter.loadDataItems(Categories.SAFE_LOCATIONS, SafeLocation::class.java)
        onDispose {
            presenter.onViewDestroyed()
        }
    }
    // the actual UI
    LoggedInTopBar(navController) {
        // Header
        ScreenHeaderTop(stringResource(R.string.SafeLocationsHeader))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when {
                // different states we have separate UIs for them
                isLoading -> CircularProgressIndicator()
                errorMessage != null -> Text("Error: $errorMessage")
                showEmptyState -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "You have not added any safe locations yet.",
                            fontFamily = myFont
                        )
                        AddLocationsButton(navController)
                    }
                }
                // this means we have a contact list (at least 1)
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
//                        item {
//                            Text(
//                                text = stringResource(R.string.SafeLocationsHeader),
//                                color = backgroundAccent,
//                                fontSize = 30.sp,
//                                fontWeight = FontWeight.Bold,
//                                fontFamily = myFont
//                            )
//                            Spacer(modifier = Modifier.height(16.dp))
//                        }
                        items(locations, key = {it.id})  { contact ->
                            LocationCard(
                                location = contact,
                                onDelete = {
                                    presenter.deleteDataItem(Categories.SAFE_LOCATIONS, contact.id)
                                },
                                // if we are editing pass in the contact id associated with it
                                onEdit = {
                                    navController.navigate("add_or_edit_safe_locations?dataItemId=${contact.id}")
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            AddLocationsButton(navController)
                        }
                    }
                }
            }
        }
        BackButton(navController)
    }
}


@Composable
fun AddLocationsButton(navController: NavHostController) {
    Button(
        onClick = { navController.navigate("add_or_edit_safe_locations") },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.addSafeLocationButton))
    }
}
@Composable
fun LocationCard(location: SafeLocation, onDelete: () -> Unit, onEdit: () -> Unit) {
    // UI for a single "Safe Location Card"
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Name
                Text(
                    text = location.safeLocationName,
                    fontFamily = myFont,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Address
                Row {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Address",
                        tint = Color.Black,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = location.safeLocationAddress,
                        fontFamily = myFont,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Notes
                Text(
                    text = location.safeLocationDescription,
                    fontFamily = myFont,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            // edit and delete button
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End

            ) {
                IconButton(onClick = { onEdit() }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Save Location",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(onClick = { onDelete() }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Save Location",
                        tint = MaterialTheme.colorScheme.error // Use error color for destructive actions
                    )
                }
            }
        }
    }
}
