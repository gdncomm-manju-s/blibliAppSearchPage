package com.example.bliblihomepage.model

data class Product(
    val name: String,
    val price: Price,
    val brand: String?,
    val review: Review?,
    val tags: List<String> = emptyList(),
    val location: String?,
    val badge: Badge?,
    val soldCountTotal: Int = 0,
    val uspLabelsTags: List<String> = emptyList(),
    val images: List<String> = emptyList()
)

data class Price(
    val priceDisplay: String?,
    val strikeThroughPriceDisplay: String?,
    val discount: Int = 0,
    val discountPrice: Double = 0.0,
    val minPrice: Double? = null,
    val offerPriceDisplay: String?,
    val isPriceRange: Boolean = false,
    val listPrice: Int? = null,
    val salePrice: Int? = null
)

data class Review(
    val rating: Int = 0,
    val count: Int = 0,
    val absoluteRating: Double = 0.0,
    val sellerRating: Double = 0.0,
    val isNewSeller: Boolean = false
)

data class Badge(
    val logisticBadge_stock: String? = null,
    val merchantBadgeUrl: String? = null,
    val merchantBadge: String? = null,
    val logisticBadge: String? = null
)


