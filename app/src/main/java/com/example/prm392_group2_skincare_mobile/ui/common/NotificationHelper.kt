// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/ui/common/NotificationHelper.kt
package com.example.prm392_group2_skincare_mobile.ui.common

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.prm392_group2_skincare_mobile.R

// A helper object to encapsulate notification creation logic.
object NotificationHelper {

    private const val CHANNEL_ID = "cart_channel"
    private const val CHANNEL_NAME = "Cart Reminders"
    private const val NOTIFICATION_ID = 1

    // Creates a notification channel (required for Android 8.0 Oreo and above).
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                // Use IMPORTANCE_HIGH to make the notification more prominent.
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders about items in your shopping cart"
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Builds and displays the cart notification.
    fun showCartNotification(context: Context) {
        // Before showing a notification on Android 13+, we must check for the POST_NOTIFICATIONS permission.
        // If it's not granted, the app could crash or the notification will fail to display.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Log a warning and return without showing the notification if permission is missing.
            // The logic to request the permission should be handled by the calling Activity/Fragment.
            Log.w("NotificationHelper", "POST_NOTIFICATIONS permission not granted. Cannot show notification.")
            return
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_cart) // Ensure you have this icon in your drawables
            .setContentTitle("Items in Your Cart!")
            .setContentText("Don't forget to complete your purchase.")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Set priority to high for better visibility.
            .setAutoCancel(true) // Dismiss the notification when the user taps on it

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}