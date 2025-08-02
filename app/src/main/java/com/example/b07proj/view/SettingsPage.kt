package com.example.b07proj.view

import android.app.AlarmManager
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.b07proj.notifs.KEY_ALARM_HOUR
import com.example.b07proj.notifs.KEY_ALARM_INTERVAL_MS
import com.example.b07proj.notifs.KEY_ALARM_MIN
import com.example.b07proj.notifs.PREFS_NAME
import com.example.b07proj.notifs.PeriodicReminderManager
import com.example.b07proj.ui.theme.BackgroundColor

// Settings page for the app
@Composable
fun SettingsPage(navController: NavHostController) {
    UISettingsPage(navController)
}

// UI for settings page
@Composable
fun UISettingsPage(navController: NavHostController) {
    // sharedPref lets the user store data in the app (not tied to account; local)
    val sharedPref = navController.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // interval = time between notifications in terms of days
    val interval = remember { mutableLongStateOf(sharedPref.getLong(KEY_ALARM_INTERVAL_MS, AlarmManager.INTERVAL_DAY)) }

    // hour and minute are the exact hour/min the notification will happen
    val hour = remember { mutableIntStateOf(sharedPref.getInt(KEY_ALARM_HOUR, 12 + 3)) }
    val minute = remember { mutableIntStateOf(sharedPref.getInt(KEY_ALARM_MIN, 30)) }

    // Associate string with time
    val dayToTime = mapOf(
        "Daily" to AlarmManager.INTERVAL_DAY,
        "Weekly" to AlarmManager.INTERVAL_DAY * 7,
        "Monthly" to AlarmManager.INTERVAL_DAY * 31
    )

    // Associate time to string!
    val timeToDay = mapOf(
        AlarmManager.INTERVAL_DAY to "Daily" ,
        AlarmManager.INTERVAL_DAY * 7 to "Weekly",
        AlarmManager.INTERVAL_DAY * 31 to "Monthly"
    )

    // List of options for the user to choose from
    val options = listOf("Daily", "Weekly", "Monthly")
    var selectedIndex by remember {
        mutableIntStateOf(options.indexOf(timeToDay[interval.longValue]))
    }

    LoggedInTopBar(
        navController = navController
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifs",
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Notification Settings",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = myFont
                )
            )
        }

        Row {
            Text(
                text = "Notification Interval:",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = myFont
                )
            )
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = timeToDay[interval.longValue] ?: "Unknown",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = myFont,
                    color = BackgroundColor
                )
            )
        }

        Column {
            SingleChoiceSegmentedButtonRow {
                options.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                        onClick = {
                            selectedIndex = index
                            println("$label was pressed")
                            println(dayToTime[label]!!)
                            // First, update the remembered variable (so the UI reflects changes)
                            interval.longValue = dayToTime[label]!!
                            // Then, change the variables into saved preferences
                            PeriodicReminderManager.instance?.setAlarmInterval(dayToTime[label]!!, hour.intValue, minute.intValue)
                        },
                        selected = index == selectedIndex,
                        label = { Text (
                            text = label,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = myFont
                            )
                        ) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.padding(16.dp))

        Row {
            Text(
                text = "Notification time:",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = myFont
                )
            )
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = "${hour.intValue}:${minute.intValue}",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = myFont,
                    color = BackgroundColor
                )
            )
        }
        // Required to toggle showing/hiding the time picker
        val showDialog = remember { mutableStateOf(false) }

        Button(
            onClick = {
                showDialog.value = true
            },
            // color is white
            colors = ButtonDefaults.buttonColors(
                containerColor = androidx.compose.ui.graphics.Color.White
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = BackgroundColor
            )
        ) {
            Text(
                text = "Set time",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = myFont
                ),
                color = BackgroundColor
            )
        }

        // Show time picker only if we say so
        if (showDialog.value)
        {
            TimePickerDialog(
                navController.context,
                { _, pickedHour: Int, pickedMinute: Int ->
                    hour.intValue = pickedHour
                    minute.intValue = pickedMinute

                    // Update the hour, min to shared prefs
                    PeriodicReminderManager.instance?.setAlarmInterval(interval.longValue, hour.intValue, minute.intValue)
                    showDialog.value = false
                },
                3, // Sets default hour to 3
                30, // Sets default min to 30
                true // Has to be true for 24-hour format
            ).show()
        }
    }
}

@Preview(showBackground = true, name = "Settings Page Preview")
@Composable
fun SettingsPagePreview() {
    SettingsPage(navController = rememberNavController())
}