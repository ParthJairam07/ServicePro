package com.example.b07proj.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.example.b07proj.ui.theme.BackgroundColor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

// #TODO make this font static + accessable for any UI
//val myFont = FontFamily(Font(R.font.afacad_regular))

// Defines the screen for the safety plan quiz
@Composable
fun SafetyPlanQuizPage1(navController: NavHostController) {
    UISafetyPlanQuiz(navController)
}

// UI for the safety plan quiz
@Composable
fun UISafetyPlanQuiz(navController: NavHostController) {
    Scaffold(
        // Top bar longs the logo and other buttons
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(vertical = 12.dp)
                    .padding(top = 24.dp)
                    .fillMaxWidth()
            ) {
                // Temporary until we get the logo
                Text("Logo goes here!")
                Row() {
                    // Settings button
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(36.dp, 32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Home",
                            tint = BackgroundColor
                        )
                    }
                    // Profile button
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(36.dp, 32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PersonOutline,
                            contentDescription = "Home",
                            tint = BackgroundColor
                        )
                    }
                }
            }
        },
    ) { padding ->
        // Column for main page content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Before anything, divide the top bar from the main content
            HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
            // Page header
            ScreenHeader("Help Us Understand You")

            // Row for icon and text telling user about data privacy
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Home",
                    modifier = Modifier.padding(end = 8.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text("The information you provide stays anonymous and local to your device.", style = TextStyle(fontFamily = myFont, fontWeight = FontWeight(600)))
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Relationship status question
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // List of options. Should be read from json #TODO
                val options = listOf("Still in a relationship", "Planning to leave", "Post-separation" )
                var selectedOption by remember { mutableIntStateOf(-1) }

                // Component for question
                QuizQuestion("What is your current relationship status?", true)

                // Component for each option
                options.forEachIndexed { index, text ->
                    QuizRadioOption(
                        text = text,
                        selectedOption == index,
                        onClick = {
                            selectedOption = index;
                        }
                    )
                }
            }
            // Add a Row to host the Button and push it to the end
            Row(
                modifier = Modifier.fillMaxWidth(), // Make the Row take full width
                horizontalArrangement = Arrangement.End // Align content of the Row to the End (right)
            ) {
                // "Done" button
                Done(navController, "update this!")
            }
        }
    }
}

// Defines the question (in text) to be answered by the user
@Composable
fun QuizQuestion(text: String, required: Boolean = false) {
    Text(
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontFamily = myFont, fontWeight = FontWeight.Bold, color = Color.Black, fontSize=16.sp)) {
                append(text)
            }
            if (required)
            {
                withStyle(style = SpanStyle(fontFamily = myFont, fontWeight = FontWeight.Bold, color = Color.Red, fontSize=16.sp)) {
                    append(" *")
                }
            }
        },
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

// Defines a single radio option for a multiple choice question
@Composable
fun QuizRadioOption(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            modifier = Modifier
                .size(28.dp)    // This also gets rid of the extra bloated space these things use
                .scale(0.75f),  // This shrinks the radio button's size physically
            onClick = onClick,
            selected = selected,
        )
        Spacer(Modifier.width(8.dp))

        Text(
            text = text,
            style = TextStyle(fontFamily = myFont, fontWeight = FontWeight(500))
        )
    }
}

// Preview the component within android studio
@Preview(showBackground = true, name = "Safety Plan Quiz Preview")
@Composable
fun SafetyPlanQuizPreview() {
    SafetyPlanQuizPage1(navController = rememberNavController()) // Use rememberNavController for preview
}