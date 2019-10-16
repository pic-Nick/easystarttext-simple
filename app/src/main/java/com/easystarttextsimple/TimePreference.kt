package com.easystarttextsimple

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference

class TimePreference(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {
    private var mTime: Int = 0
    private val mDialogLayoutResId = R.layout.pref_dialog_time

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.dialogPreferenceStyle)

    constructor(context: Context) : this(context, null)

    fun getTime(): Int {
        return mTime
    }

    fun setTime(time: Int) {
        mTime = time    // Save to Shared Preferences
        persistInt(time)
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        // Default value from attribute. Fallback value is set to 0.
        return a.getInt(index, 0)
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        // Read the value. Use the default value if it is not possible.
        setTime(getPersistedInt(mTime))
    }

    override fun getDialogLayoutResource(): Int {
        return mDialogLayoutResId
    }
}