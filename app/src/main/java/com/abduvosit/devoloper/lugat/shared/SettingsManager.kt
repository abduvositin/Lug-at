package com.abduvosit.devoloper.lugat.shared

import android.content.Context
import androidx.core.content.edit

class SettingsManager( val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getSetting(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun saveSetting(key: String, value: String) {
        sharedPreferences.edit { putString(key, value) }
    }
}
