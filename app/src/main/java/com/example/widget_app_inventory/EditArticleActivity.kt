package com.example.widget_app_inventory

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.widget_app_inventory.data.InventoryRepository
import com.example.widget_app_inventory.model.Item
import kotlinx.coroutines.launch

class EditArticleActivity : ComponentActivity() {

    private val repo by lazy { InventoryRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val itemId = intent?.getLongExtra("itemId", -1L) ?: -1L
        setContent {
            EditArticleScreen(itemId)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditArticleScreen(itemId: Long) {
        var nombre by remember { mutableStateOf("") }
        var precio by remember { mutableStateOf("") }
        var cantidad by remember { mutableStateOf("") }
        var loadedId by remember { mutableStateOf<Long?>(null) }
        val itemState = produceState<Item?>(initialValue = null, itemId) {
            value = repo.getItem(itemId)
        }
        val item = itemState.value
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(item) {
            item?.let {
                loadedId = it.id
                nombre = it.name
                precio = it.price.toString()
                cantidad = it.quantity.toString()
            }
        }

        val isNameValid = nombre.isNotBlank() && nombre.length <= 40
    val isPriceValid = precio.isNotBlank() && precio.length <= 20 && precio.count { it == '.' } <= 1 && precio.all { c -> c.isDigit() || c == '.' }
        val isQtyValid = cantidad.isNotBlank() && cantidad.length <= 4 && cantidad.all { c -> c.isDigit() }
        val canEdit = isNameValid && isPriceValid && isQtyValid

        Scaffold(
            topBar = {
                TopAppBar(
                    // Título y botón de navegación
                    title = { Text("Editar producto", color = colorResource(id = R.color.TextPrimary)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            startActivity(Intent(this@EditArticleActivity, ItemDetailActivity::class.java)
                                .putExtra("itemId", itemId))
                            finish()
                        }) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = colorResource(id = R.color.TextPrimary)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorResource(id = R.color.Surface)
                    )
                )
            },
            containerColor = colorResource(id = R.color.Background)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                // Mostrar ID del artículo
                Text(
                    text = "Id: ${loadedId ?: "-"}",
                    color = colorResource(id = R.color.TextPrimary),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Nombre
                OutlinedTextField(
                    value = nombre,
                    // permitir hasta 40 caracteres
                    onValueChange = { if (it.length <= 40) nombre = it },
                    label = { Text("Nombre artículo", color = colorResource(id = R.color.TextPrimary)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = colorResource(id = R.color.TextPrimary)),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(id = R.color.Primary),
                        unfocusedBorderColor = colorResource(id = R.color.TextSecondary),
                        cursorColor = colorResource(id = R.color.Primary),
                        focusedLabelColor = colorResource(id = R.color.TextPrimary),
                        unfocusedLabelColor = colorResource(id = R.color.TextSecondary)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Precio
                OutlinedTextField(
                    value = precio,
                    onValueChange = {
                        // permitir dígitos y un punto decimal como máximo
                        if (it.length <= 20 && it.count { ch -> ch == '.' } <= 1 && it.all { ch -> ch.isDigit() || ch == '.' }) precio = it
                    },
                    label = { Text("Precio", color = colorResource(id = R.color.TextPrimary)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = colorResource(id = R.color.TextPrimary)),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(id = R.color.Primary),
                        unfocusedBorderColor = colorResource(id = R.color.TextSecondary),
                        cursorColor = colorResource(id = R.color.Primary),
                        focusedLabelColor = colorResource(id = R.color.TextPrimary),
                        unfocusedLabelColor = colorResource(id = R.color.TextSecondary)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Cantidad
                OutlinedTextField(
                    value = cantidad,
                    // permitir solo dígitos y un máximo de 4 caracteres
                    onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) cantidad = it },
                    label = { Text("Cantidad", color = colorResource(id = R.color.TextPrimary)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = colorResource(id = R.color.TextPrimary)),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(id = R.color.Primary),
                        unfocusedBorderColor = colorResource(id = R.color.TextSecondary),
                        cursorColor = colorResource(id = R.color.Primary),
                        focusedLabelColor = colorResource(id = R.color.TextPrimary),
                        unfocusedLabelColor = colorResource(id = R.color.TextSecondary)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botón Editar
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val idLong = loadedId ?: return@launch
                            val priceDouble = precio.toDoubleOrNull() ?: 0.0
                            val qty = cantidad.toIntOrNull() ?: 0
                            val updated = Item(id = idLong, name = nombre, price = priceDouble, quantity = qty)
                            val ok = repo.updateItem(updated)
                            if (ok) {
                                try {
                                    val mgr = android.appwidget.AppWidgetManager.getInstance(this@EditArticleActivity)
                                    val ids = mgr.getAppWidgetIds(android.content.ComponentName(this@EditArticleActivity, InventoryWidgetProvider::class.java))
                                    val update = Intent(this@EditArticleActivity, InventoryWidgetProvider::class.java).apply {
                                        action = android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
                                        putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                                    }
                                    sendBroadcast(update)
                                } catch (_: Throwable) { }

                                startActivity(Intent(this@EditArticleActivity, InventoryListActivity::class.java))
                                finish()
                            }
                        }
                    },
                    enabled = canEdit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.Primary),
                        disabledContainerColor = colorResource(id = R.color.TextSecondary)
                    )
                ) {
                    Text(
                        "Editar",
                        color = colorResource(id = R.color.TextPrimary),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}