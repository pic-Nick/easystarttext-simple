package com.easystarttextsimple

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceDialogFragmentCompat

class ContactPreferenceDialog : PreferenceDialogFragmentCompat() {
    private val REQUEST_PICK_CONTACT = 1200
    private lateinit var mContactPhone: EditText
    private lateinit var mContactButton: Button
    private val phoneRegex = "[^0-9+]".toRegex()

    companion object {
        fun newInstance(key: String) : ContactPreferenceDialog {
            val fragment = ContactPreferenceDialog()
            val b = Bundle(1)
            b.putString(ARG_KEY, key)
            fragment.arguments = b
            return fragment
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val phone = mContactPhone.text.toString()

            // Get the related Preference and save the value
            val preference = preference
            if (preference is ContactPreference) {
                // This allows the client to ignore the user value.
                if (preference.callChangeListener(phone)) {
                    // Save the value
                    preference.setContact(phone)
                }
            }
        }
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        mContactPhone = view.findViewById(R.id.editText) ?: throw  IllegalStateException("Dialog view must contain a EditText with id 'editText'")

        // Get the phone from the related Preference
        var contactPhone: String? = null
        val preference = preference
        if (preference is ContactPreference) {
            contactPhone = preference.getContact()
        }

        // Set the phone to the EditText
        if (contactPhone != null)
            mContactPhone.setText(contactPhone)

        mContactButton = view.findViewById(R.id.choose_contact_button)
        mContactButton.setOnClickListener {
            dialog?.ownerActivity?.let {
                if (ContextCompat.checkSelfPermission(it, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted. Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.READ_CONTACTS)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        val builder = AlertDialog.Builder(it)
                        builder.setMessage(R.string.msg_explain_contacts_permission)
                            .setCancelable(false)
                            .setNegativeButton(it.getString(R.string.button_ok)) { dialog, _ ->
                                this.requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), MY_PERMISSIONS_REQUEST_READ_CONTACTS)
                                dialog.dismiss()
                            }
                        builder.create().show()
                    } else {
                        // No explanation needed, we can request the permission.
                        this.requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), MY_PERMISSIONS_REQUEST_READ_CONTACTS)
                    }
                } else
                    sendPickContactIntent()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PICK_CONTACT) {
            var phoneNum = data?.let { getContactNumber(it) }
            try {
                phoneNum = phoneNum?.replace(phoneRegex, "")
                mContactPhone.setText(phoneNum)
            } catch (e: Exception) {
                Toast.makeText(context, "Error stripping phone number ${phoneNum}!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        fun processPermissionRequest(myPermissionsRequestCode: Int) {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted, yay! Do it.
                when (myPermissionsRequestCode) {
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS -> sendPickContactIntent()
                }
            } else {
                // permission denied, boo! Disable the functionality that depends on this permission.
                mContactButton.isEnabled = false
                Toast.makeText(context, R.string.msg_failed_contacts_permission, Toast.LENGTH_LONG).show()
            }
        }

        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> processPermissionRequest(MY_PERMISSIONS_REQUEST_READ_CONTACTS)
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun sendPickContactIntent() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        }
        startActivityForResult(intent, REQUEST_PICK_CONTACT)
    }

    private fun getContactNumber(data: Intent): String {
        var result = ""
        val contentResolver = activity?.contentResolver
        val contactUri = data.data
        if (contentResolver != null && contactUri != null) {
            val projection: Array<String> = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
            try {
                contentResolver.query(contactUri, projection, null, null, null).use { cursor ->
                    // If the cursor returned is valid, get the phone number
                    if (cursor?.moveToFirst() == true) {
                        val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        result = cursor.getString(numberIndex)
                    }
                }
            } catch (e: Exception) {
                Log.e("CT", e.message, e)
            }
        }
        return result
    }
}