package com.example.widget_app_inventory.ui.itemdetail

import com.example.widget_app_inventory.data.InventoryRepository
import com.example.widget_app_inventory.model.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class ItemDetailViewModelTest {

    private lateinit var repo: InventoryRepository
    private lateinit var viewModel: ItemDetailViewModel

    private val testItem = Item(1, "Test", 10.0, 5)

    // Crea un dispatcher de prueba
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // Sobrescribe Main con testDispatcher
        Dispatchers.setMain(testDispatcher)

        repo = mock(InventoryRepository::class.java)
        viewModel = ItemDetailViewModel(repo)
    }

    @After
    fun tearDown() {
        // Limpia Main
        Dispatchers.resetMain()
    }

    @Test
    fun `loadItem sets item`() = runTest {
        `when`(repo.getItem(1)).thenReturn(testItem)

        viewModel.loadItem(1)
        advanceUntilIdle() // avanza coroutines

        assertEquals(testItem, viewModel.item.value)
        verify(repo).getItem(1)
    }

    @Test
    fun `deleteItem calls repo and triggers callback`() = runTest {
        `when`(repo.deleteItem(1)).thenReturn(true)

        var callbackResult = false
        viewModel.deleteItem(1) { result -> callbackResult = result }
        advanceUntilIdle()

        assertTrue(callbackResult)
        verify(repo).deleteItem(1)
    }
}
