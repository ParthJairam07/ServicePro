package com.example.b07proj.view

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.b07proj.model.dataCategories.DocumentData
import com.example.b07proj.presenter.DocumentPresenter


@Composable
fun RenderDocumentPage(navController: NavHostController) {
    val presenter = remember { DocumentPresenter() }
    DocumentPage(navController = navController, presenter = presenter)
}


@Composable
fun DocumentPage(navController: NavHostController, presenter: DocumentPresenter) {
    val documents by presenter.userDocuments.collectAsState()
    val isLoading by presenter.isLoading.collectAsState()
    val context = LocalContext.current // Get context for the download manager

    var showDeleteDialog by remember { mutableStateOf(false) }
    var documentToDelete by remember { mutableStateOf<String?>(null) }

    // Fetch documents when the screen is first shown
    LaunchedEffect(Unit) {
        presenter.fetchUserDocuments()
    }

    if (showDeleteDialog && documentToDelete != null) {
        DeleteConfirmationDialog(
            documentName = documentToDelete!!,
            onConfirm = {
                presenter.deleteDocument(documentToDelete!!)
                showDeleteDialog = false
                documentToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                documentToDelete = null
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Documents",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        //empty card when its loading
        if (isLoading && documents.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        } else if (documents.isEmpty()) {
            item {
                Text(
                    "No documents found. Add one to get started!",
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
        } else {

            items(documents, key = { it.first }) { (name, data) ->
                DocumentRowItem(
                    documentName = name,
                    documentData = data,
                    onDownloadClick = {
                        // When clicked, start the download
                        startDownload(context, data.documentUrl, name, data.documentDescription)
                    },
                    onDeleteClick = {
                        documentToDelete = name // Set which document to delete
                        showDeleteDialog = true // Show the dialog
                    }
                )
            }
        }

        // --- Add Document Button ---
        item {
            Spacer(modifier = Modifier.height(16.dp))
            AddDocumentsButton(navController)
        }
    }
}


@Composable
fun DocumentRowItem(
    documentName: String,
    documentData: DocumentData,
    onDownloadClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon on the left
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = "Document Icon",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Column for text content (name, description, date)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = documentName,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = documentData.documentDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = documentData.relevantDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Download icon on the right
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "Download Document",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Document",
                    tint = MaterialTheme.colorScheme.error // Use error color for destructive actions
                )
            }
        }
    }
}

//button navigates to add document screen
@Composable
fun AddDocumentsButton(navController: NavHostController) {
    Button(
        onClick = { navController.navigate("add_documents") },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Add New Document")
    }
}

/**
 * NEW: Function to start a file download using Android's DownloadManager.
 */
private fun startDownload(
    context: Context,
    url: String,
    title: String,
    description: String
) {
    try {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(title)
            .setDescription(description)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title) // Saves to the "Downloads" folder

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Toast.makeText(context, "Starting download...", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        // Handle exceptions, e.g., if the URL is invalid
        Toast.makeText(context, "Error starting download: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

@Composable
fun DeleteConfirmationDialog(
    documentName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Document") },
        text = { Text("Are you sure you want to permanently delete '$documentName'?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}