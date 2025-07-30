package com.example.b07proj.notifs

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.b07proj.MainActivity
import com.example.b07proj.R

const val REMINDER_CHANNEL_ID = "REMINDER_CHANNEL"
const val REMINDER_CHANNEL_NAME = "Reminder Channel"
const val REMINDER_CHANNEL_DESCRIPTION = "Shown to remind user to look at plan"
const val REMINDER_NOTIFICATION_ID = 1

// These are required to tell MainActivity, when tapping the notification, to go to the tips page RIGHT after!
const val DEEP_LINK_NEXT_SCREEN_KEY = "deep_link_destination"
const val NOTIF_PAGE_TO = "tips_page"


fun createNotifChannel(context: Context) {
    // Make sure build version is greater than Oreo's
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        // This is the notif channel itself. (uses the abv constants to init it)
        val channel = NotificationChannel(REMINDER_CHANNEL_ID, REMINDER_CHANNEL_NAME, importance).apply {
            description = REMINDER_CHANNEL_DESCRIPTION
        }
        // Actually create the channel
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun sendNotificationOnAppCreate(context: Context) {
    createNotifChannel(context)

    // Create the "intent" (this is the activity that's opened when you press the notif)
    val intent = Intent(context, MainActivity::class.java).apply { // **REPLACE MainActivity if needed**
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        // Required for MainActivity to know which page to go to
        putExtra(DEEP_LINK_NEXT_SCREEN_KEY, NOTIF_PAGE_TO)
    }

    // This is required for the notification builder below
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context,
        0, // requestCode
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Create the notif that's shown on android
    val builder = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground) // **REPLACE with your actual small icon**
        .setContentTitle("Make sure you look at your plan!")
        .setContentText("Looking at your plan again can make sure you are still prepared!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent) // Use the pending intent to open the app when pressed
        .setAutoCancel(true) // Remove notification when tapped (otherwise it would stick around. ew)

    // Send the notif after building it
    with(NotificationManagerCompat.from(context)) {
        // You have to be on android 13+ otherwise you have to do funny stuff with permissions.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPerms = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED

            if (!hasPerms) println("No perms!")
        }
        // send the notif!
        notify(REMINDER_NOTIFICATION_ID, builder.build())
    }
}