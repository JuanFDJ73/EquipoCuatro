package com.example.widget_app_inventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.widget_app_inventory.data.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InventoryViewModel : ViewModel() {

    private val repo = InventoryRepository()

    private val _items = MutableStateFlow(repo.getItems())
    val items: StateFlow<List<com.example.widget_app_inventory.model.Item>> = _items

    private val _total = MutableStateFlow(repo.computeTotal())
    val total: StateFlow<Double> = _total

    private val _showBalance = MutableStateFlow(false)
    val showBalance: StateFlow<Boolean> = _showBalance

    fun toggleShow() {
        _showBalance.value = !_showBalance.value
    }

    fun refresh() {
        viewModelScope.launch {
            _items.value = repo.getItems()
            _total.value = repo.computeTotal()
        }
    }
}
