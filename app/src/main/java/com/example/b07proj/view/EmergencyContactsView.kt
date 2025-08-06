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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.b07proj.R
import com.example.b07proj.model.dataCategories.EmergencyContact
import com.example.b07proj.presenter.dataItems.Categories
import com.example.b07proj.presenter.dataItems.ViewDataItemContract
import com.example.b07proj.presenter.dataItems.ViewContactsPresenter
import com.example.b07proj.ui.theme.backgroundAccent

@Composable
fun RenderEmergencyContactPage(navController: NavHostController) {
    EmergencyContactPage(navController)
}

@Composable
fun EmergencyContactPage(navController: NavHostController) {
    // To determine if we show the spinner or not when fetching/storing data
    var isLoading by remember { mutableStateOf(true) }
    // the list of contacts to display
    var contacts by remember { mutableStateOf<List<EmergencyContact>>(emptyList()) }
    // any error we got when fetching data
    var errorMessage by remember { mutableStateOf<String?>(null) }
    // if empty contact list from users
    var showEmptyState by remember { mutableStateOf(false) }

    // set up presenter (our view is null for now)
    val presenter = remember { ViewContactsPresenter<EmergencyContact>(null) }
    // implement contract for view
    val view = remember {
        object : ViewDataItemContract.View<EmergencyContact> {
            override fun showLoading() {
                isLoading = true
            }

            override fun hideLoading() {
                isLoading = false
            }

            override fun displayContacts(fetchedContacts: List<EmergencyContact>) {
                // update contacts list
                contacts = fetchedContacts
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
                val updatedList = contacts.filterNot { it.id == dataItemId }
                contacts = updatedList
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
        presenter.loadDataItems(Categories.EMERGENCY_CONTACTS, EmergencyContact::class.java)
        onDispose {
            presenter.onViewDestroyed()
        }
    }


    // the actual UI
    LoggedInTopBar(navController) {
        // Header
        ScreenHeaderTop(stringResource(R.string.EmergencyContactsHeader))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
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
                            "You have not added any emergency contacts yet.",
                            fontFamily = myFont
                        )
                        AddContactsButton(navController)
                    }
                }
                // this means we have a contact list (at least 1)
                else -> {

                    LazyColumn(
                        modifier = Modifier.padding(5.dp).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // header
//                        item {
//                            Text(
//                                text = stringResource(R.string.EmergencyContactsHeader),
//                                color = backgroundAccent,
//                                fontSize = 30.sp,
//                                fontWeight = FontWeight.Bold,
//                                fontFamily = myFont
//                            )
//                            Spacer(modifier = Modifier.height(16.dp))
//                        }
                        // list of contacts spawn here, each contact in contacts we have a ContactCard for
                        items(contacts, key = {it.id})  { contact ->
                            ContactCard(
                                contact = contact,
                                onDelete = {
                                    presenter.deleteDataItem(Categories.EMERGENCY_CONTACTS, contact.id)
                                },
                                // if we are editing pass in the contact id associated with it
                                onEdit = {
                                    navController.navigate("add_or_edit_contacts?dataItemId=${contact.id}")
                                }
                            )
                        }
                        // the add contact button at the bottom of the list
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            AddContactsButton(navController)
                        }
                    }
                }
            }
        }
        BackButton(navController)
    }
}

@Composable
fun AddContactsButton(navController: NavHostController) {
    Button(
        onClick = { navController.navigate("add_or_edit_contacts") },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.addEmergencyContactsButton))
    }
}
@Composable
fun ContactCard(contact: EmergencyContact, onDelete: () -> Unit, onEdit: () -> Unit) {
    // UI for a single "Contact card"
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
                // contact name
                Text(
                    text = contact.contactName,
                    fontFamily = myFont,
                    fontWeight = FontWeight.Bold
                )
                // user relation to contact
                Text(
                    text = contact.contactRelation,
                    fontFamily = myFont,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                // contact phone number
                Row {
                    Icon(
                        // Phone
                        Icons.Default.Phone,
                        contentDescription = "Phone",
                        tint = Color.Black,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = contact.contactPhoneNumber,
                        fontFamily = myFont
                    )
                }
                Row {
                    // Email
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Email",
                        tint = Color.Black,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = contact.contactEmail,
                        fontFamily = myFont

                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End

            ) {
                // edit and delete button
                IconButton(onClick = { onEdit() }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Contact",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(onClick = { onDelete() }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Contact",
                        tint = MaterialTheme.colorScheme.error // Use error color for destructive actions
                    )
                }
            }
        }
    }
}