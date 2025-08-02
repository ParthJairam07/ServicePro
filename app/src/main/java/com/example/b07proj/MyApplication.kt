package com.example.b07proj

import android.app.AlarmManager
import android.app.Application
import com.example.b07proj.notifs.PeriodicReminderManager
import com.example.b07proj.notifs.sendNotificationOnAppCreate
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        // Initialize periodic reminder singleton
        PeriodicReminderManager.init(this)

        // For testing
//        PeriodicReminderManager.instance?.setAlarmInterval(/* AlarmManager.INTERVAL_DAY */ 5000L, 15, 30)
    }
}