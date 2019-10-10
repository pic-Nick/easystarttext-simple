package com.easystarttextsimple

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import androidx.preference.PreferenceManager

class SMSBReceiver : BroadcastReceiver() {
    private var listener: Listener? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val devicePhoneNumber = sharedPreferences.getString(context?.getString(R.string.pref_phone_number_key), "")!!

        if (devicePhoneNumber.isNotBlank() && intent?.action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            var smsSender: String? = ""
            var smsBody = ""
            for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                smsSender = smsMessage.displayOriginatingAddress
                smsBody += smsMessage.messageBody
            }
            if (smsSender == devicePhoneNumber)
                listener?.onTextReceived(smsBody)
        }
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    interface Listener {
        fun onTextReceived(text: String)
    }
}