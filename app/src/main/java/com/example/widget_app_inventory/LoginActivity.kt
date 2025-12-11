package com.example.widget_app_inventory

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.LottieConstants

class LoginActivity : FragmentActivity() {

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: PromptInfo
    private var isBiometricAvailable: Boolean = false

    // Si esta actividad fue iniciada desde el widget para realizar el toggle del ojo
    private var fromWidgetToggle: Boolean = false
    private var widgetToggleId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Leer si se inició desde el widget (para volver al widget y ejecutar toggle)
        fromWidgetToggle = intent.getBooleanExtra("from_widget_toggle", false)
        widgetToggleId = intent.getIntExtra("extra_widget_id", -1)

        // Si ya hay sesión guardada, entrar directamente al inventario
        val sessionPrefs = getSharedPreferences("session_prefs", MODE_PRIVATE)
        val already = sessionPrefs.getBoolean("is_logged_in", false)
        if (already) {
            startActivity(Intent(this@LoginActivity, InventoryListActivity::class.java))
            finish()
            return
        }

        // Preparar BiometricPrompt e información del mensaje
        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                // En éxito, navegar a Inventario (Inicio)
                runOnUiThread {
                    // Guardar sesión para que el usuario no tenga que iniciar sesión nuevamente
                    this@LoginActivity.getSharedPreferences("session_prefs", MODE_PRIVATE)
                        .edit().putBoolean("is_logged_in", true).apply()

                    if (fromWidgetToggle && widgetToggleId != -1) {
                        // Enviar broadcast para que el widget ejecute la acción de toggle
                        val toggleIntent = Intent(this@LoginActivity, InventoryWidgetProvider::class.java).apply {
                            action = InventoryWidgetProvider.ACTION_TOGGLE
                            putExtra(InventoryWidgetProvider.EXTRA_WIDGET_ID, widgetToggleId)
                        }
                        sendBroadcast(toggleIntent)
                        // Cerrar y volver al lanzador / widget
                        finish()
                    } else {
                        startActivity(Intent(this@LoginActivity, InventoryListActivity::class.java))
                        finish()
                    }
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Autenticación fallida", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, errString, Toast.LENGTH_SHORT).show()
                }
            }
        })

        promptInfo = PromptInfo.Builder()
            .setTitle("Autenticación con Biometría")
            .setSubtitle("Ingrese su huella digital")
            .setNegativeButtonText("Cancelar")
            .build()

        // Verificar si el dispositivo soporta biometría y tiene credenciales registradas
        val biometricManager = BiometricManager.from(this)
        val can = try {
            // Preferir autenticadores fuertes; usar BIOMETRIC_STRONG únicamente
            biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        } catch (t: NoSuchMethodError) {
            // Biblioteca/dispositivo antiguo - usar canAuthenticate sin argumentos
            biometricManager.canAuthenticate()
        }
        isBiometricAvailable = (can == BiometricManager.BIOMETRIC_SUCCESS)
        setContent {
            LoginScreen()
        }
    }

    @Composable
    fun LoginScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.Surface))
        ) {
            // Imagen ilustrativa
            Image(
                painter = painterResource(id = com.example.widget_app_inventory.R.drawable.image_login),
                contentDescription = "Ilustración",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .size(250.dp),
                contentScale = ContentScale.Fit
            )

            // Contenido centrado
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título
                Text(
                    text = "Inventory",
                    color = colorResource(id = R.color.Primary),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 16.dp)
                )

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))
                // Button(onClick = { startActivity(Intent(this@LoginActivity, InventoryListActivity::class.java)); finish() }) {
                //     Text(text = "Open Inventory")
                // }
            }

            // Huella digital
            val composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fingerprint)).value
            val progress = animateLottieCompositionAsState(
                composition = composition,
                iterations = LottieConstants.IterateForever
            ).value

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(250.dp)
                    .offset(y = (-80).dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        // Si la biometría está disponible intenta autenticar, de lo contrario usa la sesión guardada y entra
                        if (isBiometricAvailable) {
                            biometricPrompt.authenticate(promptInfo)
                        } else {
                            Toast.makeText(this@LoginActivity, "Biometría no disponible o sin huellas registradas. Accediendo sin biometría.", Toast.LENGTH_SHORT).show()
                            // Guardar sesión y continuar
                            this@LoginActivity.getSharedPreferences("session_prefs", MODE_PRIVATE)
                                .edit().putBoolean("is_logged_in", true).apply()

                            if (fromWidgetToggle && widgetToggleId != -1) {
                                val toggleIntent = Intent(this@LoginActivity, InventoryWidgetProvider::class.java).apply {
                                    action = InventoryWidgetProvider.ACTION_TOGGLE
                                    putExtra(InventoryWidgetProvider.EXTRA_WIDGET_ID, widgetToggleId)
                                }
                                sendBroadcast(toggleIntent)
                                finish()
                            } else {
                                startActivity(Intent(this@LoginActivity, InventoryListActivity::class.java))
                                finish()
                            }
                        }
                    }
            )
        }
    }
}