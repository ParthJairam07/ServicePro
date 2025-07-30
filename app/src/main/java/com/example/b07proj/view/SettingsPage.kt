package com.example.b07proj.view

import android.app.AlarmManager
import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun SettingsPage(navController: NavController) {
    UISettingsPage(navController)
}

// REALLY TEMPORARY UI
@Composable
fun UISettingsPage(navController: NavController) {
    // AlarmManager.INTERVAL_DAY, AlarmManager.INTERVAL_HOUR, AlarmManager.INTERVAL_DAY * 7, AlarmManager.INTERVAL_DAY * 31

    val options = listOf("Dayly", "Weekly", "Monthly")
    var selectedIndex by remember { mutableIntStateOf(0) }

    val sharedPref = navController.context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    var interval = sharedPref.getLong("alarm_interval_ms", AlarmManager.INTERVAL_DAY)

    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = { selectedIndex = index },
                selected = index == selectedIndex,
                label = { Text(label) }
            )
        }
    }
}

@Preview(showBackground = true, name = "Settings Page Preview")
@Composable
fun HomePagePreview() {
    SettingsPage(navController = rememberNavController())
}