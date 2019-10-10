package com.easystarttextsimple

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity() {
    var smsBReceiver: SMSBReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        smsBReceiver = SMSBReceiver()
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

    fun startActivityTap(view: View){
        intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
    }

    fun stopActivityTap(view: View){
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.msg_confirm_stop_request)
            .setPositiveButton(R.string.button_yes) { dialog, _ ->
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                val phoneNumber = Utility.tryGetPhoneNumber(this, sharedPreferences)
                if (phoneNumber != null) {
                    val command = Utility.composeStopCommand()
                    if (Utility.tryRequestSmsPermission(this, MY_PERMISSIONS_REQUEST_SEND_STOP_SMS))
                        Utility.sendSmsCommand(phoneNumber, command)
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.button_no) { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }
}
