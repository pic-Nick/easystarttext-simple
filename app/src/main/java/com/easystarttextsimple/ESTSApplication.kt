package com.easystarttextsimple

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.provider.Telephony
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner

class ESTSApplication : Application(), LifecycleObserver {
    private var smsBReceiver: SMSBReceiver? = null

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED)
            initSmsReceiver()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        smsBReceiver?.let {
            it.setListener(null)
            unregisterReceiver(it)
        }
    }

    fun initSmsReceiver() {
        if (smsBReceiver == null)
            smsBReceiver = SMSBReceiver()
        registerReceiver(smsBReceiver, IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
    }

    fun attachSmsListener(activity: Activity) {
        smsBReceiver?.setListener(object : SMSBReceiver.Listener {
            override fun onTextReceived(text: String) {
                if (Utility.tryParseStart(activity, text))
                    return
                if (Utility.tryParseStop(activity, text))
                    return
                if (Utility.tryParseStatus(activity, text))
                    return
            }
        })
    }
}