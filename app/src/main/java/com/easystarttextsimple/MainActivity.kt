package com.easystarttextsimple

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity() {
    var smsBReceiver: SMSBReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED)
            initSmsReceiver()
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
        smsBReceiver?.setListener(null)
        unregisterReceiver(smsBReceiver)
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        fun processPermissionRequest(myPermissionsRequestCode: Int) {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted, yay! Do it.
                when (myPermissionsRequestCode) {
                    MY_PERMISSIONS_REQUEST_SEND_STOP_SMS -> sendSimpleCommand(MY_PERMISSIONS_REQUEST_SEND_STOP_SMS)
                    MY_PERMISSIONS_REQUEST_SEND_STATUS_SMS -> sendSimpleCommand(MY_PERMISSIONS_REQUEST_SEND_STATUS_SMS)
                }
                initSmsReceiver()
            } else {
                // permission denied, boo! Disable the functionality that depends on this permission.
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.msg_failed_sms_permission)
                    .setCancelable(false)
                    .setNegativeButton("OK") { dialog, _ -> dialog?.dismiss() }
                builder.create().show()
            }
        }

        when (requestCode) {
            MY_PERMISSIONS_REQUEST_SEND_STOP_SMS -> processPermissionRequest(MY_PERMISSIONS_REQUEST_SEND_STOP_SMS)
            MY_PERMISSIONS_REQUEST_SEND_STATUS_SMS -> processPermissionRequest(MY_PERMISSIONS_REQUEST_SEND_STATUS_SMS)
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun sendSimpleCommand(requestCode: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.msg_confirm_request)
            .setPositiveButton(R.string.button_yes) { dialog, _ ->
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                val phoneNumber = Utility.tryGetPhoneNumber(this, sharedPreferences)
                if (phoneNumber != null) {
                    val command = when (requestCode) {
                        MY_PERMISSIONS_REQUEST_SEND_STOP_SMS -> Utility.composeStopCommand()
                        MY_PERMISSIONS_REQUEST_SEND_STATUS_SMS -> Utility.composeStatusCommand()
                        else -> ""
                    }
                    if (command.isNotBlank() && Utility.tryRequestSmsPermission(this, requestCode))
                        if (Utility.sendSmsCommand(phoneNumber, command))
                            Toast.makeText(this, R.string.msg_command_sent, Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.button_no) { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun initSmsReceiver() {
        if (smsBReceiver != null) return
        smsBReceiver = SMSBReceiver()
        registerReceiver(smsBReceiver, IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
        smsBReceiver!!.setListener(object : SMSBReceiver.Listener {
            override fun onTextReceived(text: String) {
                if (Utility.tryParseStart(this@MainActivity, text))
                    return
                if (Utility.tryParseStatus(this@MainActivity, text))
                    return
            }
        })
    }

    fun startActivityTap(view: View){
        intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
    }

    fun stopActivityTap(view: View){
        sendSimpleCommand(MY_PERMISSIONS_REQUEST_SEND_STOP_SMS)
    }

    fun statusActivityTab(view: View) {
        sendSimpleCommand(MY_PERMISSIONS_REQUEST_SEND_STATUS_SMS)
    }
}
