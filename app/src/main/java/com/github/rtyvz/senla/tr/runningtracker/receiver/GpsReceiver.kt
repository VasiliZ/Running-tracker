package com.github.rtyvz.senla.tr.runningtracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.util.Log

class GpsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action?.matches(LocationManager.PROVIDERS_CHANGED_ACTION.toRegex()) == true) {
            Log.d("gps", "on")
        } else {
            Log.d("gps", "off")
        }
    }
}