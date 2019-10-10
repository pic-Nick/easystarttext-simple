package com.easystarttextsimple

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
            sGroups?.forEach { addPreferencesFromResource(it) }
        }
    }
}