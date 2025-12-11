package com.example.widget_app_inventory.ui.itemdetail

import com.example.widget_app_inventory.data.InventoryRepository
import com.example.widget_app_inventory.model.Item
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class ItemDetailViewModelTest {

    private lateinit var repo: InventoryRepository
    private lateinit var viewModel: ItemDetailViewModel

    @Before
    fun setup() {
        repo = mock(InventoryRepository::class.java)
        viewModel = ItemDetailViewModel(repo)
    }

    @Test
    fun `loadItem sets _item with repository result`() = runTest {
        val item = Item(1L, "Apple", 5)
        `when`(repo.getItem(1L)).thenReturn(item)

        viewModel.loadItem(1L)

        val result = viewModel.item.first()
        assertEquals(item, result)
    }

    @Test
    fun `deleteItem calls repository and triggers onDone with true`() = runTest {
        `when`(repo.deleteItem(1L)).thenReturn(true)

        var callbackResult = false
        viewModel.deleteItem(1L) { success ->
            callbackResult = success
        }

        // Small delay to allow coroutine to finish (optional with runTest)
        val result = callbackResult
        verify(repo).deleteItem(1L)
        assertTrue(result)
    }

    @Test
    fun `deleteItem calls repository and triggers onDone with false`() = runTest {
        `when`(repo.deleteItem(2L)).thenReturn(false)

        var callbackResult = true
        viewModel.deleteItem(2L) { success ->
            callbackResult = success
        }

        verify(repo).deleteItem(2L)
        assertFalse(callbackResult)
    }
}
