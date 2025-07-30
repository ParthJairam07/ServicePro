package com.example.b07proj.notifs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

// Required for the alarm for PeriodicReminder to broadcast the notification
class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        println("NOTIFICATION RECEIVED")
        sendNotificationOnAppCreate(context)
    }
}