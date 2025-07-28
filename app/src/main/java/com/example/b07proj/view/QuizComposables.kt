package com.example.b07proj.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.example.b07proj.model.Question
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScreenHeader(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontFamily = myFont,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = Color.Black
        ),
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
fun RadioQuestion(question: Question, onAnswer: (String) -> Unit) {
    var selectedOption by remember { mutableIntStateOf(-1) }
    QuizQuestion(question.question, required = true)
    question.options?.forEachIndexed { index, text ->
        QuizRadioOption(
            text = text,
            selected = selectedOption == index,
            onClick = {
                selectedOption = index
                onAnswer(text)
            }
        )
    }
}

@Composable
fun DropdownQuestion(question: Question, onAnswer: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }
    QuizQuestion(question.question, required = true)
    Box {
        Button(onClick = { expanded = true }) {
            Text(
                selectedOption.ifEmpty { "Select an option" },
                style = TextStyle(fontFamily = myFont)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            question.options?.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = TextStyle(fontFamily = myFont)) },
                    onClick = {
                        selectedOption = option
                        expanded = false
                        onAnswer(option)
                    }
                )
            }
        }
    }
}

@Composable
fun FreeformQuestion(question: Question, onAnswer: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    QuizQuestion(question.question, required = true)
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(fontFamily = myFont)
    )
    Button(
        onClick = { if (text.isNotEmpty()) onAnswer(text) },
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Text("Submit", style = TextStyle(fontFamily = myFont))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateQuestion(question: Question, onAnswer: (String) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val datePickerState = remember {
        DatePickerState(
            initialSelectedDateMillis = Calendar.getInstance().timeInMillis,
            yearRange = IntRange(2025, 2030),
            locale = Locale.getDefault()
        )
    }

    QuizQuestion(question.question, required = true)
    Button(
        onClick = { showDatePicker = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            selectedDate.ifEmpty { "Select a date" },
            style = TextStyle(fontFamily = myFont)
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            selectedDate = dateFormat.format(Date(selectedMillis))
                            onAnswer(selectedDate)
                            Log.d("DateQuestion", "Selected date: $selectedDate")
                        }
                    }
                ) {
                    Text("OK", style = TextStyle(fontFamily = myFont))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", style = TextStyle(fontFamily = myFont))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun CheckboxQuestion(question: Question, onAnswer: (String) -> Unit) {
    var selectedOptions by remember { mutableStateOf(setOf<String>()) }
    QuizQuestion(question.question, required = true)
    question.options?.forEach { option ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Checkbox(
                checked = option in selectedOptions,
                onCheckedChange = { isChecked ->
                    selectedOptions = if (isChecked) {
                        selectedOptions + option
                    } else {
                        selectedOptions - option
                    }

                    val result = selectedOptions.joinToString(", ")
                    onAnswer(result)
                    Log.d("CheckboxQuestion", "Selected options: $result")
                },
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = option,
                style = TextStyle(fontFamily = myFont, fontWeight = FontWeight(500))
            )
        }
    }
}

@Composable
fun QuizQuestion(text: String, required: Boolean = false) {
    Text(
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontFamily = myFont, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)) {
                append(text)
            }
            if (required) {
                withStyle(style = SpanStyle(fontFamily = myFont, fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 16.sp)) {
                    append(" *")
                }
            }
        },
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun QuizRadioOption(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            modifier = Modifier
                .size(28.dp)
                .scale(0.75f),
            onClick = onClick,
            selected = selected
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = TextStyle(fontFamily = myFont, fontWeight = FontWeight(500))
        )
    }
}

@Composable
fun Done(
    navController: NavHostController,
    responses: Map<String, Any>,
    onDoneClicked: (Map<String, Any>) -> Unit
) {
    Button(
        onClick = { onDoneClicked(responses) },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Done", style = TextStyle(fontFamily = myFont))
    }
}