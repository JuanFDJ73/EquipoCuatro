package com.example.widget_app_inventory.ui.editarticle

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.widget_app_inventory.R
import com.example.widget_app_inventory.model.Item
import com.example.widget_app_inventory.ui.inventorylist.InventoryListActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditArticleActivity : ComponentActivity() {

    private val viewModel: EditArticleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val itemId = intent?.getLongExtra("itemId", -1L) ?: -1L
        viewModel.loadItem(itemId)

        setContent {
            val item by viewModel.item.collectAsState()
            val coroutineScope = rememberCoroutineScope()

            if (item != null) {
                EditArticleScreen(
                    item = item!!,
                    onBack = { finish() },
                    onSave = { updatedItem ->
                        coroutineScope.launch {
                            viewModel.updateItem(updatedItem) { ok ->
                                if (ok) {
                                    startActivity(Intent(this@EditArticleActivity, InventoryListActivity::class.java))
                                    finish()
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditArticleScreen(
    item: Item,
    onBack: () -> Unit,
    onSave: (Item) -> Unit
) {
    var nombre by remember { mutableStateOf(item.name) }
    var precio by remember { mutableStateOf(item.price.toString()) }
    var cantidad by remember { mutableStateOf(item.quantity.toString()) }

    val canEdit = nombre.isNotBlank() &&
            nombre.length <= 40 &&
            precio.isNotBlank() &&
            precio.count { it == '.' } <= 1 &&
            precio.all { it.isDigit() || it == '.' } &&
            cantidad.isNotBlank() &&
            cantidad.all { it.isDigit() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar producto", color = colorResource(id = R.color.TextPrimary)) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = colorResource(id = R.color.TextPrimary))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.Surface))
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
            Text("Id: ${item.id}", color = colorResource(id = R.color.TextPrimary), fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { if (it.length <= 40) nombre = it },
                label = { Text("Nombre artículo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = precio,
                onValueChange = {
                    if (it.length <= 20 && it.count { c -> c == '.' } <= 1 && it.all { c -> c.isDigit() || c == '.' }) precio = it
                },
                label = { Text("Precio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = cantidad,
                onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) cantidad = it },
                label = { Text("Cantidad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val updatedItem = item.copy(
                        name = nombre,
                        price = precio.toDoubleOrNull() ?: 0.0,
                        quantity = cantidad.toIntOrNull() ?: 0
                    )
                    onSave(updatedItem)
                },
                enabled = canEdit,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Editar", color = colorResource(id = R.color.TextPrimary))
            }
        }
    }
}
