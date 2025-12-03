package com.example.bliblihomepage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bliblihomepage.di.CartItem

@Database(entities = [CartItem::class], version = 1)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}