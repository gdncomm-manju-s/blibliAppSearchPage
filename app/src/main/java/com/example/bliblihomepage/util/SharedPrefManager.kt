package com.example.bliblihomepage.util

import android.content.Context

object SharedPrefManager {

    private const val PREF = "user_session"
    private const val KEY_EMAIL = "logged_email"
    private const val KEY_LOGGED_IN = "logged_in"

    /** LOGIN USER */
    fun login(context: Context, email: String): Boolean {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)

        // Whether user was already registered
        val isRegistered = sp.getBoolean("registered_$email", false)

        sp.edit()
            .putString(KEY_EMAIL, email)
            .putBoolean(KEY_LOGGED_IN, true)
            .putBoolean("registered_$email", true)   // mark the user as registered
            .apply()

        return isRegistered
    }

    /** LOGOUT USER */
    fun logout(context: Context) {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        sp.edit()
            .remove(KEY_EMAIL)
            .putBoolean(KEY_LOGGED_IN, false)
            .apply()
    }

    /** CHECK LOGIN */
    fun isLoggedIn(context: Context): Boolean {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return sp.getBoolean(KEY_LOGGED_IN, false)
    }

    /** GET CURRENT LOGGED-IN USER EMAIL */
    fun getEmail(context: Context): String? {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return sp.getString(KEY_EMAIL, null)
    }
}
