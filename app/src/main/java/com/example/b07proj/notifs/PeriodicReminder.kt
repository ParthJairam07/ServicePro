package com.example.b07proj.notifs

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar
import androidx.core.content.edit

const val PREFS_NAME = "app_prefs"
const val KEY_ALARM_HOUR = "alarm_hour"
const val KEY_ALARM_MIN = "alarm_min"
const val KEY_ALARM_INTERVAL_MS = "alarm_interval_ms"

class PeriodicReminderManager private constructor(context: Context) {
    private val context: Context = context.applicationContext
    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val pendingIntent: PendingIntent =
        Intent(context, NotificationReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }

    init {
        // We do this so it doesn't fire at the start
        instance = this

        alarmManager.cancel(pendingIntent)
    }

    fun setAlarmInterval(intervalMS: Long, hourOfDay: Int, minOfDay: Int) {
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minOfDay)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        // Remove any existing alarm
        alarmManager.cancel(pendingIntent)

        // Set to new time!
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis /* System.currentTimeMillis() */,
            intervalMS,
            pendingIntent
        )
        saveAlarmTimePreference(context, hourOfDay, minOfDay, intervalMS)
    }

    private fun saveAlarmTimePreference(context: Context, hour: Int, min: Int, intervalMS: Long ) {

        println("?? hour: $hour")
        println("?? intervalMS: $intervalMS")

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putInt(KEY_ALARM_MIN, min)
            putInt(KEY_ALARM_HOUR, hour)
            putLong(KEY_ALARM_INTERVAL_MS, intervalMS)
        }

        val a = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getLong(KEY_ALARM_INTERVAL_MS, AlarmManager.INTERVAL_DAY)
        println("?? a: $a")
    }


    // Required for singleton! However we expose a way to initialize the manager "once"
    companion object {
        var instance: PeriodicReminderManager? = null

        fun init(context: Context) {
            if (instance != null) return;
            instance = PeriodicReminderManager(context)
        }
    }
}