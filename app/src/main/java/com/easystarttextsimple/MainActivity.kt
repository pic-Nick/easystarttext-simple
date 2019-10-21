package com.easystarttextsimple

import android.os.Bundle
import androidx.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.easystarttextsimple.ui.home.HomeFragment

class MainActivity : AppCompatActivity(), HomeFragment.OnHomeFragmentEventListener {
    override fun startButtonEvent() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stopButtonEvent() {
        sendSimpleCommand(MY_PERMISSIONS_REQUEST_SEND_STOP_SMS)
    }

    override fun statusButtonEvent() {
        sendSimpleCommand(MY_PERMISSIONS_REQUEST_SEND_STATUS_SMS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_timers, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
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
}
