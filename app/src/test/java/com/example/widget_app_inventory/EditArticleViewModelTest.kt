package com.example.widget_app_inventory.ui.editarticle

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
import kotlin.test.assertNull
import kotlinx.coroutines.Dispatchers

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class EditArticleViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val repo = mock(InventoryRepository::class.java)
    private lateinit var viewModel: EditArticleViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = EditArticleViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadItem sets item`() = runTest {
        val item = Item(1, "Test", 10.0, 1)
        `when`(repo.getItem(1L)).thenReturn(item)

        assertNull(viewModel.item.value) // al inicio es null
        viewModel.loadItem(1L)
        advanceUntilIdle() // espera a que la coroutine termine

        assertEquals(item, viewModel.item.value)
    }

    @Test
    fun `updateItem calls repo and returns success`() = runTest {
        val updated = Item(2, "Updated", 20.0, 2)
        `when`(repo.updateItem(updated)).thenReturn(true)

        var callbackResult: Boolean? = null
        viewModel.updateItem(updated) { success ->
            callbackResult = success
        }
        advanceUntilIdle()

        verify(repo).updateItem(updated)
        assertEquals(true, callbackResult)
    }
}
