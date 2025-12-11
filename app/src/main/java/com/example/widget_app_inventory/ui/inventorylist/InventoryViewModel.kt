package com.example.widget_app_inventory.ui.inventorylist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.widget_app_inventory.data.InventoryRepository
import com.example.widget_app_inventory.model.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repo: InventoryRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total

    private val _showBalance = MutableStateFlow(false)
    val showBalance: StateFlow<Boolean> = _showBalance

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        refresh()
    }

    fun toggleShow() {
        _showBalance.value = !_showBalance.value
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(200)
            val list = repo.getItems()
            _items.value = list
            _total.value = repo.computeTotal(list)
            _isLoading.value = false
        }
    }

    fun insertItem(item: Item, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            repo.insertItem(item)
            refresh()
            onDone?.invoke()
        }
    }
}
