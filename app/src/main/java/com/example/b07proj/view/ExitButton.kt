package com.example.b07proj.view

import android.app.Activity
import android.content.Intent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.net.toUri

@Composable
fun ExitButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activity = context as? Activity // in requirements that context is an activity

    Button(
        onClick = {
            // Launch google.com
            Intent(Intent.ACTION_VIEW, "https://www.google.com".toUri())
                .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                .also { context.applicationContext.startActivity(it) }

            // terminate
            activity?.finishAndRemoveTask()
        },
        modifier = modifier, // in other pages
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF86E6E),
            contentColor   = Color.Black
        )
    ) {
        Text("Exit", fontWeight = FontWeight.Bold)
    }
}
