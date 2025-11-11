package com.example.widget_app_inventory.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.widget_app_inventory.model.Item

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY id DESC")
    suspend fun getAll(): List<Item>

    @Insert
    suspend fun insert(item: Item): Long
}
