package com.example.bliblihomepage.model

data class BannerResponse(
    val code: Int?,
    val status: String?,
    val data: List<BannerSection>?
)

data class BannerSection(
    val id: String?,
    val sequence: Int?,
    val blocks: List<BannerBlock>?
)

data class BannerBlock(
    val id: String?,
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
    val image: String?
)
