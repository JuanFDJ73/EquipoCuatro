package com.example.widget_app_inventory.ui.editarticle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.widget_app_inventory.data.InventoryRepository
import com.example.widget_app_inventory.model.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditArticleViewModel @Inject constructor(
    private val repo: InventoryRepository
) : ViewModel() {

    private val _item = MutableStateFlow<Item?>(null)
    val item: StateFlow<Item?> = _item

    fun loadItem(id: Long) {
        viewModelScope.launch {
            _item.value = repo.getItem(id)
        }
    }

    fun updateItem(updated: Item, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repo.updateItem(updated)
            onDone(success)
        }
    }
}
