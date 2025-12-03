package com.example.bliblihomepage.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bliblihomepage.di.CartItem

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(item: CartItem): Long

    @Query("SELECT * FROM cart_item WHERE userId = :userId ORDER BY id DESC")
    suspend fun getPagedForUser(userId: String): List<CartItem>

    @Query("SELECT * FROM cart_item WHERE userId = :userId AND productId = :productId LIMIT 1")
    suspend fun findByProductId(userId: String, productId: String): CartItem?
}