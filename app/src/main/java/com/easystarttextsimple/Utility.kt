package com.easystarttextsimple

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val MY_PERMISSIONS_REQUEST_SEND_START_SMS = 1000
const val MY_PERMISSIONS_REQUEST_SEND_STOP_SMS = 1001
const val MY_PERMISSIONS_REQUEST_SEND_STATUS_SMS = 1002
const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1003
const val MY_PERMISSIONS_REQUEST_SEND_TIMERS_SMS = 1004

const val EXTRA_SETTINGS_GROUPS = "com.easystarttextsimple.EXTRA_SETTINGS_GROUPS"
const val TAG = "Util"

class Utility {
    companion object {
        fun tryGetPhoneNumber(parentActivity: Activity, sharedPreferences: SharedPreferences): String? {
            val phoneNumber = sharedPreferences.getString(parentActivity.getString(R.string.pref_phone_number_key), null)
            return if (phoneNumber?.isNotBlank() == true) phoneNumber else {
                Toast.makeText(parentActivity, R.string.msg_need_configure_phone, Toast.LENGTH_LONG).show()
//                parentActivity.startActivity(Intent(parentActivity, SettingsActivity::class.java)
//                    .putExtra(EXTRA_SETTINGS_GROUPS, intArrayOf(R.xml.msg_preferences)))
                null
            }
        }

        fun tryRequestSmsPermission(parentActivity: Activity, requestCode : Int) : Boolean {
            if (ContextCompat.checkSelfPermission(parentActivity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted. Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(parentActivity, Manifest.permission.SEND_SMS)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    val builder = AlertDialog.Builder(parentActivity)
                    builder.setMessage(R.string.msg_explain_sms_permission)
                        .setCancelable(false)
                        .setNegativeButton(parentActivity.getString(R.string.button_ok)) { dialog, _ ->
                            ActivityCompat.requestPermissions(parentActivity, arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS), requestCode)
                            dialog.dismiss()
                        }
                    builder.create().show()
                    return false
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(parentActivity, arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS), requestCode)
                    return false
                }
            }
            return true
        }

        fun tryParseStart(parentActivity: Activity, smsText: String): Boolean {
            if (smsText.toUpperCase() != parentActivity.getString(R.string.sms_start))
                return false
            val builder = AlertDialog.Builder(parentActivity)
            builder.setMessage(parentActivity.getString(R.string.msg_oper_started))
                .setTitle(R.string.msg_operation_status)
                .setCancelable(true)
                .setNegativeButton(parentActivity.getString(R.string.button_ok)) { dialog, _ ->
                    dialog.dismiss()
                }
            builder.create().show()
            return true
        }

        fun tryParseStop(parentActivity: Activity, smsText: String): Boolean {
            if (smsText.toUpperCase() != parentActivity.getString(R.string.sms_stop))
                return false
            val builder = AlertDialog.Builder(parentActivity)
            builder.setMessage(parentActivity.getString(R.string.msg_oper_stopped))
                .setTitle(R.string.msg_operation_status)
                .setCancelable(true)
                .setNegativeButton(parentActivity.getString(R.string.button_ok)) { dialog, _ ->
                    dialog.dismiss()
                }
            builder.create().show()
            return true
        }

        fun tryParseStatus(parentActivity: Activity, smsText: String): Boolean {
            val prtOn = parentActivity.getString(R.string.sms_status_on)
            val prtOff = parentActivity.getString(R.string.sms_status_off)
            val prtErr = parentActivity.getString(R.string.sms_status_error)
            var outputMessage = ""
            val smsTextLines = smsText.split('\n')
            if (smsTextLines[0].startsWith(prtOn)) {
                if (smsTextLines[0].endsWith(parentActivity.getString(R.string.sms_status_ws)))
                    outputMessage = parentActivity.getString(R.string.msg_oper_running_with_salon)
                else if (smsTextLines[0].endsWith(parentActivity.getString(R.string.sms_status_wos)))
                    outputMessage = parentActivity.getString(R.string.msg_oper_running_without_salon)
            } else if (smsTextLines[0].startsWith(prtOff)) {
                outputMessage = parentActivity.getString(R.string.msg_oper_off)
            } else if (smsTextLines[0].startsWith(prtErr)) {
                outputMessage = parentActivity.getString(R.string.msg_oper_error)
            } else
                return false
            if (smsTextLines.count() > 1) {
                val tVal = extractValue(smsTextLines[1], parentActivity.getString(R.string.sms_status_t))
                if (tVal != null)
                    outputMessage += parentActivity.getString(R.string.msg_oper_temperature) + " $tVal."
            }
            if (smsTextLines.count() > 2) {
                val vVal = extractValue(smsTextLines[2], parentActivity.getString(R.string.sms_status_v))
                if (vVal != null)
                    outputMessage += parentActivity.getString(R.string.msg_oper_voltage) + " $vVal."
            }

            val builder = AlertDialog.Builder(parentActivity)
            builder.setMessage(outputMessage)
                .setTitle(R.string.msg_operation_status)
                .setCancelable(true)
                .setNegativeButton(parentActivity.getString(R.string.button_ok)) { dialog, _ ->
                    dialog.dismiss()
                }
            builder.create().show()
            return true
        }

        fun sendSmsCommand(phone: String, command: String) : Boolean {
            return try {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phone, null, command, null, null)
                true
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                false
            }
        }

        fun composeStartCommand(startDuration: String, warmUpSalon: Boolean): String {
            return "${startDuration}${if (warmUpSalon) "#" else "*"}"
        }

        fun composeStopCommand(): String {
            return "00*"
        }

        fun composeStatusCommand(): String {
            return "09*"
        }

        fun composeSetTimerCommand(timers: Timers): String {
            return "09*"
        }

        private fun extractValue(line: String, param: String): String? {
            val pair = line.split('=')
            return if (pair.count() == 2 && pair[0] == param) pair[1] else null
        }
    }
}