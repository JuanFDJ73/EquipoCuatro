package com.example.widget_app_inventory.data

import android.content.Context
import com.example.widget_app_inventory.model.Item

class InventoryRepository(private val context: Context) {

    private val db by lazy { AppDatabase.getInstance(context) }

    suspend fun getItems(): List<Item> {
        return try {
            db.itemDao().getAll()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun insertItem(item: Item): Long {
        return db.itemDao().insert(item)
    }

    fun computeTotal(items: List<Item>): Double = items.sumOf { it.price * it.quantity }
}
