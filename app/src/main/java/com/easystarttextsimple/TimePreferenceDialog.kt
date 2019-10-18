package com.easystarttextsimple

import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.View
import android.widget.TimePicker
import androidx.preference.PreferenceDialogFragmentCompat

class TimePreferenceDialog : PreferenceDialogFragmentCompat() {
    private lateinit var mTimePicker: TimePicker

    companion object {
        fun newInstance(key: String) : TimePreferenceDialog {
            val fragment = TimePreferenceDialog()
            val b = Bundle(1)
            b.putString(ARG_KEY, key)
            fragment.arguments = b
            return fragment
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            // generate value to save
            val hours = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) mTimePicker.currentHour else mTimePicker.hour
            val minutes = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) mTimePicker.currentMinute else mTimePicker.minute
            val minutesAfterMidnight = hours * 60 + minutes

            // Get the related Preference and save the value
            val preference = preference
            if (preference is TimePreference) {
                // This allows the client to ignore the user value.
                if (preference.callChangeListener(minutesAfterMidnight)) {
                    // Save the value
                    preference.setTime(minutesAfterMidnight)
                }
            }
        }
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        mTimePicker = view.findViewById(R.id.edit) ?: throw  IllegalStateException("Dialog view must contain a TimePicker with id 'edit'")

        // Get the time from the related Preference
        var minutesAfterMidnight: Int? = null
        val preference = preference
        if (preference is TimePreference) {
            minutesAfterMidnight = preference.getTime()
        }
        mTimePicker.setIs24HourView(is24HourFormat(context))

        // Set the time to the TimePicker
        if (minutesAfterMidnight != null) {
            val hours = minutesAfterMidnight / 60
            val minutes = minutesAfterMidnight % 60
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                mTimePicker.currentHour = hours
                mTimePicker.currentMinute = minutes
            } else {
                mTimePicker.hour = hours
                mTimePicker.minute = minutes
            }
        }
    }
}