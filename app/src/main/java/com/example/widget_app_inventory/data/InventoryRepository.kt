package com.example.widget_app_inventory.data

import com.example.widget_app_inventory.model.Item

class InventoryRepository {

    // Temporary in-memory sample data. Replace with DB/network later.
    private val sample = listOf(
        Item(1, "Widget A", 12500.0, 10),
        Item(2, "Widget B", 49999.99, 2),
        Item(3, "Widget C", 1000.0, 3)
    )

    fun getItems(): List<Item> = sample

    fun computeTotal(): Double = sample.sumOf { it.price * it.quantity }
}
