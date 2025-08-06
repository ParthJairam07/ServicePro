package com.example.b07proj.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.b07proj.R
import com.example.b07proj.ui.theme.BackgroundColor
import com.example.b07proj.ui.theme.Primary40
import com.example.b07proj.ui.theme.White

/*
This file defines common components for reuse throughout all UI
 */

// Defines the screen header text that most screens have at the top.
@Composable
fun ScreenHeaderTop(text: String) {
    val myFont = FontFamily(Font(R.font.afacad))

    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(bottom = 16.dp),
            fontFamily = myFont,
            fontSize = 32.sp,
            color = Color(0xFFA83E92),
            textAlign = TextAlign.Center
        )
    }
}

// The button at the bottom right of most screens. Used to go to the next screen.
@Composable
fun Done(navController: NavHostController, goTo: String) {
    Button(
        onClick = {
            navController.navigate(goTo)
        },
        enabled = true,
        colors = ButtonDefaults.buttonColors(containerColor = BackgroundColor),
        modifier = Modifier
            .height(40.dp)
            .width(100.dp),
        shape = RoundedCornerShape(8.dp), // Even smaller radius
        contentPadding = PaddingValues(horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Done",
                color = White, fontSize = 20.sp, fontFamily = myFont
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Home",
            )
        }
    }
}

// composable to just go back to the previous screen
@Composable
fun BackButton(navController: NavHostController, optionalText: String = stringResource(R.string.BackButtonText), modifier: Modifier = Modifier) {
    Button(
        onClick = {
            navController.popBackStack()
//            navController.navigate("home_page")
        },
        modifier = modifier.padding(top = 16.dp),

        // make it have a border
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(1.dp, Primary40),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary40),
    ) {
        Image(
            painter = painterResource(id = R.drawable.whitearrowgoback),
            contentDescription = stringResource(id = R.string.arrow_content_description),
            modifier = Modifier.size(16.dp),

            colorFilter = ColorFilter.tint(Primary40)

        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = optionalText,
        )
    }
}