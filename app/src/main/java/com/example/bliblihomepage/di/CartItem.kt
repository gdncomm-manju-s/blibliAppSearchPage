package com.example.bliblihomepage.di

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_item")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: String,
    val productId: String,
    val productJson: String
)
