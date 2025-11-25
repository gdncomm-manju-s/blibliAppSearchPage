package com.example.bliblihomepage.util

import com.example.bliblihomepage.R
import okhttp3.logging.HttpLoggingInterceptor

object AppConfig {

    // API / Network
    const val API_TIMEOUT_SECONDS = 30L
    val API_LOG_LEVEL = HttpLoggingInterceptor.Level.BODY

    const val BASE_URL = "https://www.blibli.com/"
    const val DEFAULT_SEARCH_TERM = "samsung"

    const val PAGE_SIZE = 10
    const val LOAD_DELAY_MS = 1500L

    // Mock
    const val MOCK_PRODUCT_FILE = "mock_products.json"
    const val MOCK_PRODUCTS_PATH_KEY = "products"

    const val HTTP_OK = 200
    const val HTTP_OK_MSG = "OK"
    const val MIME_JSON = "application/json"

    // Errors
    const val ERROR_FETCH_PRODUCTS = "Failed to fetch products"
    // placeholders (drawable resource names)
   val PLACEHOLDER_RES = R.drawable.placeholder_image
    val ERROR_RES = R.drawable.placeholder_image
    // View types
    const val VIEW_TYPE_ITEM = 1
    const val VIEW_TYPE_LOADER = 2

    // USP tags
    const val TAG_FREE_SHIPPING = "FREE_SHIPPING"
    const val TAG_2HD_1 = "2HD"
    const val TAG_2HD_2 = "2_JAM"
    const val TAG_2HD_3 = "2HOUR"
    const val TAG_2HD_4 = "2_JAM_SAMPAI"

    // Brand
    const val BRAND_NO_BRAND = "no brand"

    // Dimensions (dp)
    const val USP_TAG_WIDTH_DP = 40
    const val USP_TAG_HEIGHT_DP = 16
    const val DEFAULT_IMAGE_WIDTH_DP = 135
    const val LOCATION_PADDING_DP = 60

    // Price ratios
    const val PRICE_RATIO_STRIKE = 0.28f
    const val PRICE_RATIO_PRICE = 0.55f

    // Search & pagination
    const val MIN_SEARCH_LENGTH = 3
    const val PAGINATION_THRESHOLD = 3

    // Search fields
    const val SEARCH_FIELD_NAME = "name"
    const val SEARCH_FIELD_BRAND = "brand"
    const val SEARCH_FIELD_LOCATION = "location"
}
