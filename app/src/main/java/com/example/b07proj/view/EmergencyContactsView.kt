package com.example.b07proj.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.b07proj.R
import com.example.b07proj.model.EmergencyContact
import com.example.b07proj.presenter.contacts.ViewContactsContract
import com.example.b07proj.presenter.contacts.ViewContactsPresenter
import com.example.b07proj.ui.theme.backgroundAccent

@Composable
fun RenderEmergencyContactPage(navController: NavHostController) {
    EmergencyContactPage(navController)
}

@Composable
fun EmergencyContactPage(navController: NavHostController) {
    var isLoading by remember { mutableStateOf(true) }
    var contacts by remember { mutableStateOf<List<EmergencyContact>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showEmptyState by remember { mutableStateOf(false) }

    // set up presenter
    val presenter = remember { ViewContactsPresenter(null) }
    // implement contract for view
    val view = remember {
        object : ViewContactsContract.View {
            override fun showLoading() {
                isLoading = true
            }

            override fun hideLoading() {
                isLoading = false
            }

            override fun displayContacts(fetchedContacts: List<EmergencyContact>) {
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
        }
    }
    DisposableEffect(presenter) {
        presenter.view = view
        // Load data when the view is ready
        presenter.loadContacts()
        onDispose {
            presenter.onViewDestroyed()
        }
    }
    LoggedInTopBar(navController) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                errorMessage != null -> Text("Error: $errorMessage")
                showEmptyState -> {
                    Column(
                        modifier = Modifier.padding(5.dp).fillMaxSize(),
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
                else -> {

                    LazyColumn(
                        modifier = Modifier.padding(5.dp).fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            Text(
                                text = stringResource(R.string.EmergencyContactsHeader),
                                color = backgroundAccent,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = myFont
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        items(contacts)  { contact ->
                            ContactCard(contact = contact)

                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            AddContactsButton(navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddContactsButton(navController: NavHostController) {
    Button(
        onClick = { navController.navigate("add_contacts") },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.addEmergencyContactsButton))
    }
}
@Composable
fun ContactCard(contact: EmergencyContact) {
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
                Text(
                    text = contact.contactName,
                    fontFamily = myFont,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = contact.contactRelation,
                    fontFamily = myFont,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
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
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Contact",
                    tint = MaterialTheme.colorScheme.error // Use error color for destructive actions
                )
            }
        }
    }
}