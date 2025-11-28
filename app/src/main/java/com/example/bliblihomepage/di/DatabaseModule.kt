package com.example.bliblihomepage.di

import android.content.Context
import com.example.bliblihomepage.di.CartDatabase
import com.example.bliblihomepage.di.CartDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCartDatabase(@ApplicationContext context: Context): CartDatabase =
        CartDatabase.getInstance(context)

    @Provides
    fun provideCartDao(db: CartDatabase): CartDao = db.cartDao()
}
