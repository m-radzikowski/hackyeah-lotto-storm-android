package com.blasthack.storm.lottostorm

import android.content.Context
import android.preference.PreferenceManager

class PreferencesHelper(context: Context){
    companion object {
        val DEVELOP_MODE = false
        private const val DEVICE_TOKEN = "data.source.prefs.DEVICE_TOKEN"
        private const val MY_ID = "data.source.prefs.MY_ID"
    }
    private val preferences = PreferenceManager   .getDefaultSharedPreferences(context)
    // save device token
    var deviceToken = preferences.getString(DEVICE_TOKEN, "")
        set(value) = preferences.edit().putString(DEVICE_TOKEN,     value).apply()

    var myId = preferences.getString(MY_ID, "")
        set(value) = preferences.edit().putString(MY_ID,     value).apply()
}