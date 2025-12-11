package com.example.widget_app_inventory.ui.login

import android.app.Application
import android.content.SharedPreferences
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.junit.Assert.*

class LoginViewModelTest {

    private lateinit var app: Application
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        // Mocks
        app = mock(Application::class.java)
        prefs = mock(SharedPreferences::class.java)
        editor = mock(SharedPreferences.Editor::class.java)

        // Configurar comportamiento del editor
        `when`(prefs.edit()).thenReturn(editor)
        `when`(editor.putBoolean(anyString(), anyBoolean())).thenReturn(editor)

        // Configurar Application para devolver prefs
        `when`(app.getSharedPreferences(anyString(), anyInt())).thenReturn(prefs)

        // Instanciar ViewModel
        viewModel = LoginViewModel(app)
    }

    @Test
    fun `isLoggedIn returns true when prefs says logged in`() {
        `when`(prefs.getBoolean("is_logged_in", false)).thenReturn(true)

        val result = viewModel.isLoggedIn()

        assertTrue(result)
    }

    @Test
    fun `isLoggedIn returns false when prefs says logged out`() {
        `when`(prefs.getBoolean("is_logged_in", false)).thenReturn(false)

        val result = viewModel.isLoggedIn()

        assertFalse(result)
    }

    @Test
    fun `login sets is_logged_in to true`() {
        viewModel.login()

        // Verificar que se llamó al editor con el valor correcto
        verify(editor).putBoolean("is_logged_in", true)
        verify(editor).apply()
    }
}
