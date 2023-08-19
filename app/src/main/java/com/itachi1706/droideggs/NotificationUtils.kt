package com.itachi1706.droideggs

import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

object NotificationUtils {
    fun canSendNotification(context: Context): Boolean {
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationManager.areNotificationsEnabled()
        } else {
            true // NO-OP before then
        }

    }

    const val TAG = "NotificationUtils"

    fun requestNotificationPermission(rationale: String, context: AppCompatActivity, notifLauncher: ActivityResultLauncher<String>) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return // No need call as only 33 onwards need perms
        Log.d(TAG, "Requesting Notification Permission as above TIRAMISU")

        if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) return // Already granted
        Log.d(TAG, "Requesting Notification Permission as not granted")

        if (context.shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
            Log.d(TAG, "Requesting Notification Permission as should show rationale")
            Toast.makeText(context, rationale, Toast.LENGTH_LONG).show()
        }
        Log.d(TAG, "Requesting Notification Permission as launching")
        notifLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    fun showNotificationSettings(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Notification Permission")
            .setMessage("Notification permission is required, Please allow notification permission from setting")
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${context.packageName}")
                context.startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}