package com.example.bliblihomepage.util

import android.content.Context

object SharedPrefManager {

    private const val PREF = "user_session"
    private const val KEY_EMAIL = "logged_email"
    private const val KEY_LOGGED_IN = "logged_in"

    private const val KEY_PASSWORD_PREFIX = "password_"
    private const val KEY_REGISTERED_PREFIX = "registered_"

    /** SIGN UP USER */
    fun signup(context: Context, username: String, password: String) {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        sp.edit()
            .putString(KEY_PASSWORD_PREFIX + username, password)
            .putBoolean(KEY_REGISTERED_PREFIX + username, true)
            .apply()
    }

    /** LOGIN USER - RETURNS true only if correct password */
    fun login(context: Context, username: String, password: String): Boolean {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)

        val isRegistered = sp.getBoolean(KEY_REGISTERED_PREFIX + username, false)
        if (!isRegistered) return false

        val savedPassword = sp.getString(KEY_PASSWORD_PREFIX + username, null)
        if (savedPassword == null || savedPassword != password) return false

        sp.edit()
            .putString(KEY_EMAIL, username)
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()

        return true
    }

    /** LOGOUT */
    fun logout(context: Context) {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        sp.edit()
            .remove(KEY_EMAIL)
            .putBoolean(KEY_LOGGED_IN, false)
            .apply()
    }

    /** CHECK LOGIN STATE */
    fun isLoggedIn(context: Context): Boolean {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return sp.getBoolean(KEY_LOGGED_IN, false)
    }

    /** GET CURRENT LOGGED USER */
    fun getEmail(context: Context): String? {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return sp.getString(KEY_EMAIL, null)
    }

    fun isRegistered(context: Context, username: String): Boolean {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return sp.getBoolean(KEY_REGISTERED_PREFIX + username, false)
    }

}
