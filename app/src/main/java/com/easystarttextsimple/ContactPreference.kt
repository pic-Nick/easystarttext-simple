package com.easystarttextsimple

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference

class ContactPreference(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {
    private var mContactPhone: String = ""
    private val mDialogLayoutResId = R.layout.pref_dialog_contact

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.dialogPreferenceStyle)

    constructor(context: Context) : this(context, null)

    fun getContact(): String {
        return mContactPhone
    }

    fun setContact(contact: String) {
        mContactPhone = contact    // Save to Shared Preferences
        persistString(contact)
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        // Default value from attribute. Fallback value is set to "".
        return a.getString(index) ?: ""
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        // Read the value. Use the default value if it is not possible.
        setContact(getPersistedString(mContactPhone))
    }

    override fun getDialogLayoutResource(): Int {
        return mDialogLayoutResId
    }
}