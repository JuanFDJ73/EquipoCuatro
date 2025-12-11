package com.example.widget_app_inventory.ui.login

import android.app.Application
import android.content.Context
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.junit.Assert.*

class LoginViewModelTest {

    private lateinit var app: Application
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        app = mock(Application::class.java)
        val prefs = mock(android.content.SharedPreferences::class.java)
        val editor = mock(android.content.SharedPreferences.Editor::class.java)

        `when`(app.getSharedPreferences(anyString(), anyInt())).thenReturn(prefs)
        `when`(prefs.getBoolean("is_logged_in", false)).thenReturn(true)
        `when`(prefs.edit()).thenReturn(editor)
        `when`(editor.putBoolean(anyString(), anyBoolean())).thenReturn(editor)

        viewModel = LoginViewModel(app)
    }

    @Test
    fun `isLoggedIn returns true when prefs says logged in`() {
        assertTrue(viewModel.isLoggedIn())
    }
}
