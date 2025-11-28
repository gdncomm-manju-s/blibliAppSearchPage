package com.example.bliblihomepage.model
data class BannerResponse(
    val code: Int?,
    val status: String?,
    val data: List<BannerSection>?   // <-- REAL API RETURNS ARRAY
)

data class BannerSection(
    val id: String?,                 // "MAIN_SECTION"
    val sequence: Int?,
    val blocks: List<BannerBlock>?
)

data class BannerBlock(
    val id: String?,                 // "MAIN_CAROUSEL"
    val sequence: Int?,
    val components: List<BannerComponent>?
)

data class BannerComponent(
    val id: String?,
    val name: String?,
    val parameters: List<BannerParameter>?
)

data class BannerParameter(
    val id: String?,
    val image: String?               // <-- The banner image URL
)
