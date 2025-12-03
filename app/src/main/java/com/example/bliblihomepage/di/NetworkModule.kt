package com.example.bliblihomepage.di

import com.example.bliblihomepage.network.ProductApiService
import com.example.bliblihomepage.network.BannerApiService
import com.example.bliblihomepage.util.AppConfig
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.google.gson.GsonBuilder
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .callTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(ok: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL)
            .client(ok)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideBannerApi(retrofit: Retrofit): BannerApiService =
        retrofit.create(BannerApiService::class.java)

    @Provides
    @Singleton
    fun provideProductApi(retrofit: Retrofit): ProductApiService =
        retrofit.create(ProductApiService::class.java)
}

