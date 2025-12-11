package com.example.widget_app_inventory.ui.editarticle

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
class EditArticleViewModelTest {

    private lateinit var repo: InventoryRepository
    private lateinit var viewModel: EditArticleViewModel

    @Before
    fun setup() {
        repo = mock(InventoryRepository::class.java)
        viewModel = EditArticleViewModel(repo)
    }

    @Test
    fun `loadItem sets _item with repository result`() = runTest {
        val item = Item(1L, "Laptop", 2)
        `when`(repo.getItem(1L)).thenReturn(item)

        viewModel.loadItem(1L)

        val result = viewModel.item.first()
        assertEquals(item, result)
    }

    @Test
    fun `updateItem calls repository and triggers onDone with true`() = runTest {
        val updated = Item(1L, "Laptop", 3)
        `when`(repo.updateItem(updated)).thenReturn(true)

        var callbackResult = false
        viewModel.updateItem(updated) { success ->
            callbackResult = success
        }

        verify(repo).updateItem(updated)
        assertTrue(callbackResult)
    }

    @Test
    fun `updateItem calls repository and triggers onDone with false`() = runTest {
        val updated = Item(2L, "Phone", 5)
        `when`(repo.updateItem(updated)).thenReturn(false)

        var callbackResult = true
        viewModel.updateItem(updated) { success ->
            callbackResult = success
        }

        verify(repo).updateItem(updated)
        assertFalse(callbackResult)
    }
}
