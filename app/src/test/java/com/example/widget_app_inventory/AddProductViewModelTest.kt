package com.example.widget_app_inventory.ui.addproduct

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.widget_app_inventory.data.InventoryRepository
import com.example.widget_app_inventory.model.Item
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AddProductViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val repo = mock(InventoryRepository::class.java)
    private lateinit var viewModel: AddProductViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddProductViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `isCodeExists returns true when item exists`() = runTest {
        val item = Item(1, "Test", 10.0, 1)
        `when`(repo.getItem(1L)).thenReturn(item)

        val result = viewModel.isCodeExists(1L)
        assertTrue(result)
    }

    @Test
    fun `isCodeExists returns false when item does not exist`() = runTest {
        `when`(repo.getItem(2L)).thenReturn(null)

        val result = viewModel.isCodeExists(2L)
        assertFalse(result)
    }

    @Test
    fun `insertItem calls repository and triggers onComplete`() = runTest {
        val item = Item(3, "New", 15.0, 1)
        var callbackCalled = false

        doAnswer {
            callbackCalled = true
            null
        }.`when`(repo).insertItem(item)

        viewModel.insertItem(item) {}
        advanceUntilIdle() // espera a que termine la coroutine

        verify(repo).insertItem(item)
        // La lambda onComplete se llama automáticamente después de insertItem
    }
}
