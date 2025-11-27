package com.example.bliblihomepage.util

import com.example.bliblihomepage.R
import okhttp3.logging.HttpLoggingInterceptor

object AppConfig {

    // API / Network
    val API_LOG_LEVEL = HttpLoggingInterceptor.Level.BODY

    const val BASE_URL = "https://www.blibli.com/"
    const val DEFAULT_SEARCH_TERM = "samsung"

    const val PAGE_SIZE = 60
    const val LOAD_DELAY_MS = 1500L

    // Errors
    const val ERROR_FETCH_PRODUCTS = "Failed to fetch products"

    // placeholders (drawable resource names)
    val PLACEHOLDER_RES = R.drawable.ic_cart
    val ERROR_RES = R.drawable.placeholder_image

    // Search & pagination
    const val MIN_SEARCH_LENGTH = 3
}
