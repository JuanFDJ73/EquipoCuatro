package com.example.widget_app_inventory

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.google.firebase.firestore.FirebaseFirestore


class AddProductActivity : ComponentActivity() {
    private val db = FirebaseFirestore.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm = androidx.lifecycle.ViewModelProvider(this, androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application))[com.example.widget_app_inventory.viewmodel.InventoryViewModel::class.java]

        setContent {
            AddProductScreen(onBack = {
                startActivity(Intent(this, InventoryListActivity::class.java))
                finish()
            }, onSave = { codigo, nombre, precio, cantidad ->
                // Crear el item y guardarlo
                val priceDouble = precio.toDoubleOrNull() ?: 0.0
                val qty = cantidad.toIntOrNull() ?: 0
                val item = com.example.widget_app_inventory.model.Item(
                    id = 0,
                    codigo = codigo,
                    name = nombre,
                    price = priceDouble,
                    quantity = qty
                )
                vm.insertItem(item) {
                    // Guardar también en Firestore
                    saveToFirestore(codigo, nombre, priceDouble, qty)
                    
                    // Notificar widgets para refrescar
                    try {
                        val mgr = android.appwidget.AppWidgetManager.getInstance(this)
                        val ids = mgr.getAppWidgetIds(android.content.ComponentName(this, InventoryWidgetProvider::class.java))
                        val update = Intent(this, InventoryWidgetProvider::class.java).apply {
                            action = android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
                            putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                        }
                        sendBroadcast(update)
                    } catch (t: Throwable) {
                        // ignore
                    }

                    // Despues de guardar, volver a la lista
                    startActivity(Intent(this, InventoryListActivity::class.java))
                    finish()
                }
            })
        }
    }

    private fun saveToFirestore(codigo: String, nombre: String, precio: Double, cantidad: Int) {
        // Obtener usuario actual
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            android.util.Log.e("Firestore", "Usuario no autenticado")
            return
        }

        val userId = currentUser.uid

        val productData = hashMapOf(
            "codigo" to codigo,
            "nombre" to nombre,
            "precio" to precio,
            "cantidad" to cantidad,
            "timestamp" to com.google.firebase.Timestamp.now()
        )

        // Guardar en Firestore: usuarios/{userId}/IdProductos/{codigo}
        db.collection("usuarios")
            .document(userId)
            .collection("IdProductos")
            .document(codigo)
            .set(productData)
            .addOnSuccessListener {
                android.util.Log.d("Firestore", "Producto guardado con ID: $codigo")
            }
            .addOnFailureListener { e ->
                android.util.Log.e("Firestore", "Error guardando producto", e)
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(onBack: () -> Unit, onSave: (codigo: String, nombre: String, precio: String, cantidad: String) -> Unit) {
    var codigo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }

    val isFormValid = codigo.isNotBlank() && nombre.isNotBlank() && precio.isNotBlank() && cantidad.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar producto", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF424242))
            )
        },
        containerColor = Color(0xFF2A2A2A)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = codigo,
                onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) codigo = it },
                label = { Text("Código producto", color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { if (it.length <= 40) nombre = it },
                label = { Text("Nombre artículo", color = Color.White) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = precio,
                onValueChange = { if (it.length <= 20 && it.all(Char::isDigit)) precio = it },
                label = { Text("Precio", color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = cantidad,
                onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) cantidad = it },
                label = { Text("Cantidad", color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { if (isFormValid) onSave(codigo, nombre, precio, cantidad) },
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800),
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Guardar",
                    color = Color.White,
                    fontWeight = if (isFormValid) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 16.sp
                )
            }
        }
    }
}
