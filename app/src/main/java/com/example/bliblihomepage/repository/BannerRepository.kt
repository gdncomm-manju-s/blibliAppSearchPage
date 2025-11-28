package com.example.bliblihomepage.repository

import com.example.bliblihomepage.model.BannerResponse
import com.example.bliblihomepage.network.BannerApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BannerRepository @Inject constructor(
    private val api: BannerApiService
) {

    suspend fun getBannerImages(): List<String> = withContext(Dispatchers.IO) {
        try {
            val response = api.getBanners()

            val sections = response.data ?: return@withContext emptyList()

            val images = mutableListOf<String>()

            sections.forEach { section ->
                section.blocks?.forEach { block ->
                    block.components?.forEach { comp ->
                        comp.parameters?.forEach { param ->
                            param.image?.let { images.add(it) }
                        }
                    }
                }
            }

            return@withContext images.distinct()

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
