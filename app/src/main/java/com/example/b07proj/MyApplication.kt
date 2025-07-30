package com.example.b07proj

import android.app.AlarmManager
import android.app.Application
import com.example.b07proj.notifs.PeriodicReminderManager
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        println("TESTING NOTIFS")

        // Set to "day" by default
        PeriodicReminderManager.init(context = this);

        PeriodicReminderManager.instance?.setAlarmInterval(/* AlarmManager.INTERVAL_DAY */ 5000L)
    }
}