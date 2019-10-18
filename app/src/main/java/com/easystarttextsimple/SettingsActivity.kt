package com.easystarttextsimple

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
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
            val phonePreference: ContactPreference? = findPreference(getString(R.string.pref_phone_number_key))
            phonePreference?.summaryProvider = Preference.SummaryProvider<ContactPreference> { preference ->
                val contact = preference.getContact()
                if (contact.isEmpty())
                    getString(R.string.phone_number_pref_notset_hint)
                else
                    contact
            }
        }

        override fun onDisplayPreferenceDialog(preference: Preference?) {
            // Try if the preference is one of our custom Preferences
            var dialogFragment: DialogFragment? = null
            if (preference is TimePreference) {
                // Create a new instance of TimePreferenceDialogFragment with the key of the related Preference
                dialogFragment = TimePreferenceDialog.newInstance(preference.key)
            } else if (preference is ContactPreference) {
                dialogFragment = ContactPreferenceDialog.newInstance(preference.key)
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