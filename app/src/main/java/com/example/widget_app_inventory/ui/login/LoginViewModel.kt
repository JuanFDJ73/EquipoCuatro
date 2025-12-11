package com.example.widget_app_inventory.ui.login

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val app: Application
) : ViewModel() {

    fun isLoggedIn(): Boolean {
        val prefs = app.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("is_logged_in", false)
    }

    fun login() {
        app.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)
            .edit().putBoolean("is_logged_in", true).apply()
    }
}
