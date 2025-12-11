package com.example.widget_app_inventory.ui.inventorylist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.widget_app_inventory.data.InventoryRepository
import com.example.widget_app_inventory.model.Item
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class InventoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val repo = mock(InventoryRepository::class.java)
    private lateinit var viewModel: InventoryViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = InventoryViewModel(repo, mock(android.app.Application::class.java))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refresh loads items and total`() = runTest {
        val items = listOf(Item(1, "Test", 10.0, 1))
        `when`(repo.getItems()).thenReturn(items)
        `when`(repo.computeTotal(items)).thenReturn(10.0)

        viewModel.refresh()
        advanceUntilIdle() // espera a que todas las coroutines terminen

        assertEquals(items, viewModel.items.value)
        assertEquals(10.0, viewModel.total.value, 0.0)
    }

    @Test
    fun `insertItem calls repo and refreshes`() = runTest {
        val item = Item(2, "New", 5.0, 1)
        val items = listOf(item)
        `when`(repo.getItems()).thenReturn(items)
        `when`(repo.computeTotal(items)).thenReturn(5.0)

        viewModel.insertItem(item)
        advanceUntilIdle()

        assertEquals(items, viewModel.items.value)
        assertEquals(5.0, viewModel.total.value, 0.0)

        // Verificamos que insertItem fue llamado
        verify(repo).insertItem(item)
    }

    @Test
    fun `toggleShow flips showBalance`() = runTest {
        val initial = viewModel.showBalance.value
        viewModel.toggleShow()
        assertEquals(!initial, viewModel.showBalance.value)
        viewModel.toggleShow()
        assertEquals(initial, viewModel.showBalance.value)
    }
}
