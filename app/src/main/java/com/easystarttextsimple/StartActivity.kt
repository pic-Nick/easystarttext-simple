package com.easystarttextsimple

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)
        val settingsGroup = intent.getIntExtra(EXTRA_SETTINGS_GROUPS, R.xml.start_preferences)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.startPrefsLayout, StartSettingsFragment(settingsGroup))
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        val estsApp = this.application
        if (estsApp is ESTSApplication) estsApp.attachSmsListener(this)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        fun processPermissionRequest(myPermissionsRequestCode: Int) {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted, yay! Do it.
                when (myPermissionsRequestCode) {
                    MY_PERMISSIONS_REQUEST_SEND_START_SMS -> sendStartCommand()
                }
                val estsApp = this.application
                if (estsApp is ESTSApplication) {
                    estsApp.initSmsReceiver()
                    estsApp.attachSmsListener(this)
                }
            } else {
                // permission denied, boo! Disable the functionality that depends on this permission.
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.msg_failed_sms_permission)
                    .setCancelable(false)
                    .setNegativeButton(R.string.button_ok) { dialog, _ -> dialog?.dismiss() }
                builder.create().show()
            }
        }

        when (requestCode) {
            MY_PERMISSIONS_REQUEST_SEND_START_SMS -> processPermissionRequest(MY_PERMISSIONS_REQUEST_SEND_START_SMS)
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun sendStartCommandTap(view: View){
        sendStartCommand()
    }

    private fun sendStartCommand() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val phoneNumber = Utility.tryGetPhoneNumber(this, sharedPreferences)
        if (phoneNumber != null) {
            val startDuration = sharedPreferences.getString(getString(R.string.pref_duration_key), "20")!!
            val warmUpSalon = sharedPreferences.getBoolean(getString(R.string.pref_warm_salon_key), false)
            val command = Utility.composeStartCommand(startDuration, warmUpSalon)
            if (Utility.tryRequestSmsPermission(this, MY_PERMISSIONS_REQUEST_SEND_START_SMS))
                if (Utility.sendSmsCommand(phoneNumber, command))
                    Toast.makeText(this, R.string.msg_command_sent, Toast.LENGTH_LONG).show()
        }
    }

    class StartSettingsFragment(settingsGroup: Int) : PreferenceFragmentCompat() {
        private val sGroup = settingsGroup

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            try {
                setPreferencesFromResource(sGroup, rootKey)
            } catch (e: Exception) {
                Log.e("START", e.message, e)
            }
        }
    }
}