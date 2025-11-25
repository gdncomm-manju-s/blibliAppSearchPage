package com.example.bliblihomepage.model

data class ProductSearchResponse(
    val code: Int,
    val status: String,
    val data: ProductSearchData?
)

data class ProductSearchData(
    val products: List<Product> = emptyList()
)
