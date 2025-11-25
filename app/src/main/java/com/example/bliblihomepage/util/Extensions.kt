package com.example.bliblihomepage.util

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadUrlSafe(url: String?) {
    Glide.with(this.context)
        .load(url)
        .placeholder(AppConfig.PLACEHOLDER_RES)
        .error(AppConfig.ERROR_RES)
        .into(this)
}

fun Context.hideKeyboardFromWindow() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
    val view = (this as? Activity)?.currentFocus
    view?.let { imm.hideSoftInputFromWindow(it.windowToken, 0) }
}
