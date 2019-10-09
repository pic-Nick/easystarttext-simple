package com.easystarttextsimple

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.telephony.SmsManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.Exception

const val MY_PERMISSIONS_REQUEST_SEND_SMS = 1000

class Utility {
    companion object{
        fun tryRequestSmsPermission(activity: Activity) : Boolean {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.SEND_SMS)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    val builder = AlertDialog.Builder(activity)
                    builder.setMessage(R.string.msg_explain_sms_permission)
                        .setCancelable(false)
                        .setNegativeButton("OK") { dialog, _ ->
                            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.SEND_SMS), MY_PERMISSIONS_REQUEST_SEND_SMS)
                            dialog.cancel()
                        }
                    builder.create().show()
                    return false
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.SEND_SMS), MY_PERMISSIONS_REQUEST_SEND_SMS)
                    return false
                }
            }
            return true
        }

        fun sendSmsCommand(phone: String, command: String) {
            try {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phone, null, command, null, null)
            } catch (e: Exception) {
                val msg = e.message
            }
        }

        fun composeStartCommand(startDuration: String, warmUpSalon: Boolean): String {
            return "${startDuration}${if (warmUpSalon) "#" else "*"}"
        }
    }
}