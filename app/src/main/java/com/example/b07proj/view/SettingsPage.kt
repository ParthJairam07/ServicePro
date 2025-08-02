package com.example.b07proj.view

import android.app.AlarmManager
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.b07proj.notifs.KEY_ALARM_HOUR
import com.example.b07proj.notifs.KEY_ALARM_INTERVAL_MS
import com.example.b07proj.notifs.PREFS_NAME
import com.example.b07proj.notifs.PeriodicReminderManager

@Composable
fun SettingsPage(navController: NavController) {
    UISettingsPage(navController)
}

// REALLY TEMPORARY UI
@Composable
fun UISettingsPage(navController: NavController) {
    val sharedPref = navController.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val interval = sharedPref.getLong(KEY_ALARM_INTERVAL_MS, AlarmManager.INTERVAL_DAY)
    val hour = sharedPref.getInt(KEY_ALARM_HOUR, 12 + 3)



    println("interval: $interval")
    println("hour: $hour")

    // Associate string with time
    val dayToTime = mapOf(
        "Daily" to AlarmManager.INTERVAL_DAY,
        "Weekly" to AlarmManager.INTERVAL_DAY * 7,
        "Monthly" to AlarmManager.INTERVAL_DAY * 31
    )
    val timeToDay = mapOf(
        AlarmManager.INTERVAL_DAY to "Daily" ,
        AlarmManager.INTERVAL_DAY * 7 to "Weekly",
        AlarmManager.INTERVAL_DAY * 31 to "Monthly"
    )

    val options = listOf("Daily", "Weekly", "Monthly")
    var selectedIndex by remember {
        mutableIntStateOf(options.indexOf(timeToDay[interval]))
    }

    Column {
        Spacer(modifier = Modifier.padding(100.dp))


        SingleChoiceSegmentedButtonRow {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    onClick = {
                        selectedIndex = index
                        println("$label was pressed")
                        println(dayToTime[label]!!)
                        PeriodicReminderManager.instance?.setAlarmInterval(dayToTime[label]!!, hour)
                    },
                    selected = index == selectedIndex,
                    label = { Text(label) }
                )
            }
        }

    }

}

@Preview(showBackground = true, name = "Settings Page Preview")
@Composable
fun SettingsPagePreview() {
    SettingsPage(navController = rememberNavController())
}