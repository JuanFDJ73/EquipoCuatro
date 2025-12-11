package com.example.widget_app_inventory

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Si ya hay sesión guardada, entrar directamente al inventario
        val sessionPrefs = getSharedPreferences("session_prefs", MODE_PRIVATE)
        if (sessionPrefs.getBoolean("is_logged_in", false)) {
            startActivity(Intent(this, InventoryListActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        // Bind views
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)

        // Inicializar FirebaseAuth
        try {
            auth = FirebaseAuth.getInstance()
        } catch (ex: Exception) {
            Log.w("LoginActivity", "Firebase not configured: ${ex.message}")
            Toast.makeText(this, "Firebase no está configurado.", Toast.LENGTH_LONG).show()
        }

        // Real-time validation for password (mínimo 6 dígitos)
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = s?.length ?: 0
                if (length in 1..5) {
                    tilPassword.error = getString(R.string.error_min_password)
                    tilPassword.boxStrokeColor = ContextCompat.getColor(this@LoginActivity, R.color.error_red)
                } else {
                    tilPassword.error = null
                    tilPassword.boxStrokeColor = ContextCompat.getColor(this@LoginActivity, R.color.white)
                }
                updateEnabledStates()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Email text watcher
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateEnabledStates()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Login click
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            if (::auth.isInitialized) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        getSharedPreferences("session_prefs", MODE_PRIVATE)
                            .edit().putBoolean("is_logged_in", true).apply()
                        startActivity(Intent(this, InventoryListActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, getString(R.string.toast_login_incorrect), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Firebase no configurado.", Toast.LENGTH_SHORT).show()
            }
        }

        // Register click
        tvRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            if (::auth.isInitialized) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        getSharedPreferences("session_prefs", MODE_PRIVATE)
                            .edit().putBoolean("is_logged_in", true).apply()
                        startActivity(Intent(this, InventoryListActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, getString(R.string.toast_register_error), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Firebase no configurado.", Toast.LENGTH_SHORT).show()
            }
        }

        // Inicializar estados
        updateEnabledStates()
    }

    private fun updateEnabledStates() {
        val emailOk = etEmail.text.toString().trim().isNotEmpty()
        val pass = etPassword.text.toString()
        val passOk = pass.length >= 6

        // Botón Login: habilitado solo si email tiene contenido y password >= 6 dígitos
        btnLogin.isEnabled = emailOk && passOk
        if (btnLogin.isEnabled) {
            btnLogin.setTextColor(ContextCompat.getColor(this, R.color.white))
            btnLogin.setTypeface(btnLogin.typeface, Typeface.BOLD)
        } else {
            btnLogin.setTextColor(ContextCompat.getColor(this, R.color.white))
            btnLogin.setTypeface(null, Typeface.NORMAL)
        }

        // TextView Registrarse: habilitado si ambos campos tienen contenido (sin importar longitud password)
        tvRegister.isEnabled = emailOk && pass.isNotEmpty()
        if (tvRegister.isEnabled) {
            tvRegister.setTextColor(ContextCompat.getColor(this, R.color.white))
            tvRegister.setTypeface(tvRegister.typeface, Typeface.BOLD)
        } else {
            tvRegister.setTextColor(android.graphics.Color.parseColor("#9EA1A1"))
            tvRegister.setTypeface(null, Typeface.NORMAL)
        }
    }
}