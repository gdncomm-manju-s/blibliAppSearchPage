package com.example.bliblihomepage.di

import android.content.Context
import androidx.room.Room
import com.example.bliblihomepage.db.CartDao
import com.example.bliblihomepage.db.CartDatabase
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
    fun provideDatabase(@ApplicationContext context: Context): CartDatabase {
        return Room.databaseBuilder(
            context,
            CartDatabase::class.java,
            "cart_db"
        ).build()
    }

    @Provides
    fun provideCartDao(db: CartDatabase): CartDao = db.cartDao()
}
