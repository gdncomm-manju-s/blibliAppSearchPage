package com.example.bliblihomepage.di

import androidx.room.*

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItem): Long

    @Query("SELECT * FROM cart_item WHERE userId = :userId ORDER BY id DESC")
    suspend fun getPagedForUser(userId: String): List<CartItem>

    @Query("SELECT * FROM cart_item WHERE userId = :userId AND productId = :productId LIMIT 1")
    suspend fun findByProductId(userId: String, productId: String): CartItem?
}
