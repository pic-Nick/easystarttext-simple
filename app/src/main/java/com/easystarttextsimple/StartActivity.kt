package com.easystarttextsimple

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.preference.MultiSelectListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import kotlin.properties.Delegates

class StartActivity : AppCompatActivity() {

    private var settingsGroup by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)
        settingsGroup = intent.getIntExtra(EXTRA_SETTINGS_GROUPS, R.xml.start_preferences)
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
                    MY_PERMISSIONS_REQUEST_SEND_TIMERS_SMS -> sendStartCommand()
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
            when (settingsGroup) {
                R.xml.start_preferences -> {
                    val startDuration = sharedPreferences.getString(getString(R.string.pref_duration_key), "20")!!
                    val warmUpSalon = sharedPreferences.getBoolean(getString(R.string.pref_warm_salon_key), false)
                    val command = Utility.composeStartCommand(startDuration, warmUpSalon)
                    if (Utility.tryRequestSmsPermission(this, MY_PERMISSIONS_REQUEST_SEND_START_SMS))
                        if (Utility.sendSmsCommand(phoneNumber, command))
                            Toast.makeText(this, R.string.msg_command_sent, Toast.LENGTH_LONG).show()
                }
                R.xml.timers_preferences -> {
                    val timer1 = sharedPreferences.getBoolean(getString(R.string.pref_timer_1_key), false)
                    val timer1days = sharedPreferences.getStringSet(getString(R.string.pref_timer_1_days_key), setOf())
                    val timer1time = sharedPreferences.getString(getString(R.string.pref_timer_1_time_key), "")
                    val timer2 = sharedPreferences.getBoolean(getString(R.string.pref_timer_2_key), false)
                    val timer2days = sharedPreferences.getStringSet(getString(R.string.pref_timer_2_days_key), setOf())
                    val timer2time = sharedPreferences.getString(getString(R.string.pref_timer_2_time_key), "")
                    val timer3 = sharedPreferences.getBoolean(getString(R.string.pref_timer_3_key), false)
                    val timer3days = sharedPreferences.getStringSet(getString(R.string.pref_timer_3_days_key), setOf())
                    val timer3time = sharedPreferences.getString(getString(R.string.pref_timer_3_time_key), "")
                    val command = Utility.composeSetTimerCommand(Timers(TimerData(timer1, timer1days, timer1time), TimerData(timer2, timer2days, timer2time), TimerData(timer3, timer3days, timer3time)))
                    if (Utility.tryRequestSmsPermission(this, MY_PERMISSIONS_REQUEST_SEND_TIMERS_SMS))
                        if (Utility.sendSmsCommand(phoneNumber, command))
                            Toast.makeText(this, R.string.msg_command_sent, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    class StartSettingsFragment(settingsGroup: Int) : PreferenceFragmentCompat() {
        private val sGroup = settingsGroup

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            try {
                setPreferencesFromResource(sGroup, rootKey)

                arrayOf(R.string.pref_timer_1_days_key, R.string.pref_timer_2_days_key, R.string.pref_timer_3_days_key).forEach {
                    val daysPreference: MultiSelectListPreference? = findPreference(getString(it))
                    daysPreference?.summaryProvider = Preference.SummaryProvider<MultiSelectListPreference> { preference ->
                        val weekDays = preference.values
                        if (weekDays.isNotEmpty()) {
                            weekDays.sortedWith(Comparator { o1, o2 ->
                                when {
                                    preference.findIndexOfValue(o1) > preference.findIndexOfValue(o2) -> 1
                                    preference.findIndexOfValue(o1) < preference.findIndexOfValue(o2) -> -1
                                    else -> 0
                                }
                            }).joinToString(", ") { preference.entries[preference.findIndexOfValue(it)] }
                        } else
                            getString(R.string.phone_number_pref_notset_hint)
                    }
                }
            } catch (e: Exception) {
                Log.e("START", e.message, e)
            }
        }

        override fun onDisplayPreferenceDialog(preference: Preference?) {
            // Try if the preference is one of our custom Preferences
            var dialogFragment: DialogFragment? = null
            if (preference is TimePreference) {
                // Create a new instance of TimePreferenceDialogFragment with the key of the related Preference
                dialogFragment = TimePreferenceDialog.newInstance(preference.key)
            }

            // If it was one of our custom Preferences, show its dialog
            if (dialogFragment != null) {
                dialogFragment.setTargetFragment(this, 0)
                this.fragmentManager?.let { dialogFragment.show(it, "android.support.v7.preference.PreferenceFragment.DIALOG") }
            } else {
                // Could not be handled here. Try with the super method.
                super.onDisplayPreferenceDialog(preference)
            }
        }
    }
}