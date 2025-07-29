package com.example.b07proj.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
fun RenderDocumentPage(navController: NavHostController) {
    DocumentPage(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentPage(navController: NavHostController) {
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
                    text = stringResource(R.string.DocumentHeader),
                    color = backgroundAccent,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = myFont
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                AddDocumentsButton(navController)
            }
        }

    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddDocumentsButton(navController: NavHostController) {
    Button(
        colors = ButtonDefaults.buttonColors(containerColor = Primary40),
        shape = RoundedCornerShape(8.dp), // Even smaller radius
        onClick = {
            navController.navigate("add_documents")
        }
    ) {
        Text(
            text = stringResource(R.string.addDocumentButtonText),
            modifier = Modifier.padding(end = 15.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.drive_folder_upload),
            contentDescription = stringResource(id = R.string.addDocumentButtonDescription),
            modifier = Modifier.scale(2.5F)
        )

    }

}

