package com.apdef.tandurapps.storage

import android.content.Context
import android.content.SharedPreferences

class SharedPref(val context: Context) {
    companion object{
        const val USER_PREF = "user_pref"
    }
    var sharedPref = context.getSharedPreferences(USER_PREF, 0)

    fun setValues(key:String, value: String){
        val editor : SharedPreferences.Editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getValues(key:String):String? {
        return sharedPref.getString(key, "")
    }
}