package com.easystarttextsimple

import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        val settingsGroups = intent.getIntArrayExtra(EXTRA_SETTINGS_GROUPS)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment(settingsGroups))
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment(settingsGroups: IntArray?) : PreferenceFragmentCompat() {
        private val sGroups = settingsGroups

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            if (sGroups != null)
                sGroups.forEach { addPreferencesFromResource(it) }
            else {
                addPreferencesFromResource(R.xml.msg_preferences)
                addPreferencesFromResource(R.xml.start_preferences)
            }
            val phonePreference: EditTextPreference? = findPreference(getString(R.string.pref_phone_number_key))
            phonePreference?.setOnBindEditTextListener {
                editText -> editText.inputType = InputType.TYPE_CLASS_PHONE
            }
        }
    }
}