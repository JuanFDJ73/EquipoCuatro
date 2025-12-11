package com.example.widget_app_inventory.ui.inventorylist

import android.app.Application
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
class InventoryViewModelTest {

    private lateinit var repo: InventoryRepository
    private lateinit var app: Application
    private lateinit var viewModel: InventoryViewModel

    @Before
    fun setup() {
        repo = mock(InventoryRepository::class.java)
        app = mock(Application::class.java)

        // Setup repositorio por defecto
        `when`(repo.getItems()).thenReturn(emptyList())
        `when`(repo.computeTotal(anyList())).thenReturn(0.0)

        viewModel = InventoryViewModel(repo, app)
    }

    @Test
    fun `toggleShow flips showBalance value`() = runTest {
        val initial = viewModel.showBalance.first()
        viewModel.toggleShow()
        val after = viewModel.showBalance.first()
        assertEquals(!initial, after)
    }

    @Test
    fun `refresh updates items, total and isLoading`() = runTest {
        val items = listOf(Item(1, "Apple", 5), Item(2, "Banana", 3))
        `when`(repo.getItems()).thenReturn(items)
        `when`(repo.computeTotal(items)).thenReturn(8.0)

        viewModel.refresh()

        assertEquals(items, viewModel.items.first())
        assertEquals(8.0, viewModel.total.first(), 0.0)
        assertFalse(viewModel.isLoading.first())
    }

    @Test
    fun `insertItem calls repo and triggers onDone`() = runTest {
        val item = Item(3, "Orange", 2)
        var callbackCalled = false

        viewModel.insertItem(item) {
            callbackCalled = true
        }

        // Verificar que insertItem del repo se llamó
        verify(repo).insertItem(item)
        // Verificar que se llamó el callback
        assertTrue(callbackCalled)
    }
}
