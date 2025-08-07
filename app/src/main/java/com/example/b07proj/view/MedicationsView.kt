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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.example.b07proj.model.dataCategories.Medication
import com.example.b07proj.presenter.dataItems.Categories
import com.example.b07proj.presenter.dataItems.ViewDataItemContract
import com.example.b07proj.presenter.dataItems.ViewContactsPresenter
import com.example.b07proj.ui.theme.backgroundAccent

@Composable
fun RenderMedicationPage(navController: NavHostController) {
    MedicationPage(navController)
}

@Composable
fun MedicationPage(navController: NavHostController) {

    var isLoading by remember { mutableStateOf(true) }
    var medications by remember { mutableStateOf<List<Medication>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showEmptyState by remember { mutableStateOf(false) }

    // set up presenter
    val presenter = remember { ViewContactsPresenter<Medication>(null) }
    // implement contract for view
    val view = remember {
        object : ViewDataItemContract.View<Medication> {
            override fun showLoading() {
                isLoading = true
            }

            override fun hideLoading() {
                isLoading = false
            }

            override fun displayContacts(fetchedContacts: List<Medication>) {
                medications = fetchedContacts
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
                val updatedList = medications.filterNot { it.id == dataItemId }
                medications = updatedList
                if (updatedList.isEmpty()) {
                    showEmptyState = true
                }
            }
        }
    }
    DisposableEffect(presenter) {
        presenter.view = view
        // Load data when the view is ready
        presenter.loadDataItems(Categories.MEDICATIONS, Medication::class.java)
        onDispose {
            presenter.onViewDestroyed()
        }
    }
    LoggedInTopBar(navController) {
        // header
        ScreenHeaderTop(stringResource(R.string.MedicationsHeader))

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
                            "You have not added any medication yet.",
                            fontFamily = myFont
                        )
                        AddMedicationsButton(navController)
                    }
                }
                else -> {
                    // this means we have a contact list (at least 1)
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // header
//                        item {
//                            Text(
//                                text = stringResource(R.string.MedicationsHeader),
//                                color = backgroundAccent,
//                                fontSize = 30.sp,
//                                fontWeight = FontWeight.Bold,
//                                fontFamily = myFont
//                            )
//                            Spacer(modifier = Modifier.height(16.dp))
//                        }
                        // list of contacts spawn here, each contact in contacts we have a ContactCard for
                        items(medications, key = {it.id})  { contact ->
                            MedicationCard(
                                medication = contact,
                                onDelete = {
                                    presenter.deleteDataItem(Categories.MEDICATIONS, contact.id)
                                },
                                // if we are editing pass in the contact id associated with it
                                onEdit = {
                                    navController.navigate("add_or_edit_medications?dataItemId=${contact.id}")
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            AddMedicationsButton(navController)
                        }
                    }
                }
            }
        }
        BackButton(navController)
    }
}

@Composable
fun AddMedicationsButton(navController: NavHostController) {
    Button(
        onClick = { navController.navigate("add_or_edit_medications") },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.addMedicationButton))
    }
}


@Composable
fun MedicationCard(medication: Medication, onDelete: () -> Unit, onEdit: () -> Unit) {
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
                    text = medication.medicationName,
                    fontFamily = myFont,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = medication.medicationDosage,
                    fontFamily = myFont,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Icon(
                        // Phone
                        Icons.Default.CalendarMonth,
                        contentDescription = "Calendar",
                        tint = Color.Black,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = medication.medicationExpiry,
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
                IconButton(onClick = { onEdit() }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Medication",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
//                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = { onDelete() }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Medication",
                        tint = MaterialTheme.colorScheme.error // Use error color for destructive actions
                    )
                }
            }
        }
    }
}



@Preview(showBackground = true, name = "Medication Page")
@Composable
fun MedicationPagePreview() {
    MedicationPage(rememberNavController())
}