package com.example.widget_app_inventory.ui.addproduct

import com.example.widget_app_inventory.data.InventoryRepository
import com.example.widget_app_inventory.model.Item
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class AddProductViewModelTest {

    private lateinit var repository: InventoryRepository
    private lateinit var viewModel: AddProductViewModel

    @Before
    fun setup() {
        repository = mock(InventoryRepository::class.java)
        viewModel = AddProductViewModel(repository)
    }

    @Test
    fun `isCodeExists returns true when item exists`() = runTest {
        val code = 1L
        val item = Item(code, "Laptop", 2)
        `when`(repository.getItem(code)).thenReturn(item)

        val result = viewModel.isCodeExists(code)

        assertTrue(result)
    }

    @Test
    fun `isCodeExists returns false when item does not exist`() = runTest {
        val code = 2L
        `when`(repository.getItem(code)).thenReturn(null)

        val result = viewModel.isCodeExists(code)

        assertFalse(result)
    }

    @Test
    fun `insertItem calls repository and triggers callback`() = runTest {
        val item = Item(3L, "Phone", 5)
        var callbackCalled = false

        viewModel.insertItem(item) {
            callbackCalled = true
        }

        verify(repository).insertItem(item)
        assertTrue(callbackCalled)
    }
}
