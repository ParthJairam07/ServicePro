package com.example.b07proj.notifs

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock

class PeriodicReminderManager private constructor(context: Context) {

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

    fun setAlarmInterval(intervalMS: Long) {
        // Remove any existing alarm
        alarmManager.cancel(pendingIntent)

        // Set to new time!
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            SystemClock.elapsedRealtime() + intervalMS,
            intervalMS,
            pendingIntent
        )
        println("fwaf")

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