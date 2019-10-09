package com.easystarttextsimple

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.appcompat.app.AlertDialog


class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.startPrefsLayout, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_SEND_SMS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do it.
                    sendStartCommand()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(R.string.msg_failed_sms_permission)
                        .setCancelable(false)
                        .setNegativeButton("OK") { dialog, _ -> dialog?.cancel() }
                    builder.create().show()
                }
                return
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun sendStartCommandTap(view: View){
        sendStartCommand()
    }

    private fun sendStartCommand() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val phoneNumber = sharedPreferences.getString(getString(R.string.pref_phone_number_key), null)
        if (phoneNumber?.isNotBlank() == true) {
            val startDuration = sharedPreferences.getString(getString(R.string.pref_duration_key), "20")!!
            val warmUpSalon = sharedPreferences.getBoolean(getString(R.string.pref_warm_salon_key), false)
            val command = Utility.composeStartCommand(startDuration, warmUpSalon)
            if (Utility.tryRequestSmsPermission(this))
                Utility.sendSmsCommand(phoneNumber, command)
        } else {
            Toast.makeText(applicationContext, R.string.msg_need_configure_phone, Toast.LENGTH_LONG).show()
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.start_preferences, rootKey)
        }
    }
}