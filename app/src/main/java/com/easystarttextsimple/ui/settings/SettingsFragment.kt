package com.easystarttextsimple.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.easystarttextsimple.R
import com.easystarttextsimple.ui_prefs.ContactPreference
import com.easystarttextsimple.ui_prefs.ContactPreferenceDialog

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.msg_preferences)
        addPreferencesFromResource(R.xml.start_preferences)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        // Try if the preference is one of our custom Preferences
        var dialogFragment: DialogFragment? = null
        if (preference is ContactPreference) {
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