package com.example.widget_app_inventory.ui.addproduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.widget_app_inventory.data.InventoryRepository
import com.example.widget_app_inventory.model.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {

    suspend fun isCodeExists(code: Long): Boolean {
        return repository.getItem(code) != null
    }

    fun insertItem(item: Item, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertItem(item)
            onComplete()
        }
    }
}
