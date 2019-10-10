package com.easystarttextsimple

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.Exception

const val MY_PERMISSIONS_REQUEST_SEND_START_SMS = 1000
const val MY_PERMISSIONS_REQUEST_SEND_STOP_SMS = 1000

const val EXTRA_SETTINGS_GROUPS = "com.easystarttextsimple.EXTRA_SETTINGS_GROUPS"

class Utility {
    companion object{
        fun tryGetPhoneNumber(parentActivity: Activity, sharedPreferences: SharedPreferences): String? {
            val phoneNumber = sharedPreferences.getString(parentActivity.getString(R.string.pref_phone_number_key), null)
            return if (phoneNumber?.isNotBlank() == true) phoneNumber else {
                Toast.makeText(parentActivity, R.string.msg_need_configure_phone, Toast.LENGTH_LONG).show()
                parentActivity.startActivity(Intent(parentActivity, SettingsActivity::class.java)
                    .putExtra(EXTRA_SETTINGS_GROUPS, intArrayOf(R.xml.root_preferences)))
                null
            }
        }

        fun tryRequestSmsPermission(activity: Activity, requestCode : Int) : Boolean {
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
                            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.SEND_SMS), requestCode)
                            dialog.dismiss()
                        }
                    builder.create().show()
                    return false
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.SEND_SMS), requestCode)
                    return false
                }
            }
            return true
        }

        fun sendSmsCommand(phone: String, command: String) : Boolean {
            return try {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phone, null, command, null, null)
                true
            } catch (e: Exception) {
                val msg = e.message
                false
            }
        }

        fun composeStartCommand(startDuration: String, warmUpSalon: Boolean): String {
            return "${startDuration}${if (warmUpSalon) "#" else "*"}"
        }

        fun composeStopCommand(): String {
            return "00*"
        }
    }
}