package com.example.widget_app_inventory.model

import androidx.room.*

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val price: Double,
    val quantity: Int
)
