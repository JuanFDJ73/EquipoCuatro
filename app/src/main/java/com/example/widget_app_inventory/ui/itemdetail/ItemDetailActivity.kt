package com.example.widget_app_inventory.ui.itemdetail

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.widget_app_inventory.R
import com.example.widget_app_inventory.model.Item
import com.example.widget_app_inventory.ui.editarticle.EditArticleActivity
import com.example.widget_app_inventory.ui.inventorylist.InventoryListActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*
import androidx.compose.ui.unit.sp

@AndroidEntryPoint
class ItemDetailActivity : ComponentActivity() {

    private val viewModel: ItemDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val itemId = intent?.getLongExtra("itemId", -1L) ?: -1L
        viewModel.loadItem(itemId)

        setContent {
            val item by viewModel.item.collectAsState()
            val coroutineScope = rememberCoroutineScope()
            val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
            var showDialog by remember { mutableStateOf(false) }

            if (item != null) {
                ItemDetailScreen(
                    item = item!!,
                    showDialog = showDialog,
                    onBack = { finish() },
                    onEdit = {
                        startActivity(Intent(this, EditArticleActivity::class.java).putExtra("itemId", itemId))
                    },
                    onDelete = {
                        showDialog = true
                    },
                    onConfirmDelete = {
                        showDialog = false
                        coroutineScope.launch {
                            viewModel.deleteItem(itemId) { deleted ->
                                if (deleted) {
                                    // Actualizar widgets tras la eliminación
                                    try {
                                        val mgr = android.appwidget.AppWidgetManager.getInstance(this@ItemDetailActivity)
                                        val ids = mgr.getAppWidgetIds(android.content.ComponentName(this@ItemDetailActivity, com.example.widget_app_inventory.InventoryWidgetProvider::class.java))
                                        val update = Intent(this@ItemDetailActivity, com.example.widget_app_inventory.InventoryWidgetProvider::class.java).apply {
                                            action = android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
                                            putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                                        }
                                        sendBroadcast(update)
                                    } catch (_: Throwable) {}

                                    startActivity(Intent(this@ItemDetailActivity, InventoryListActivity::class.java))
                                    finish()
                                }
                            }
                        }
                    },
                    onDismissDelete = {
                        showDialog = false
                    },
                    currencyFormatter = currencyFormatter
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    item: Item,
    showDialog: Boolean,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    currencyFormatter: NumberFormat
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Detalle del producto", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = colorResource(id = R.color.TextPrimary))
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = colorResource(id = R.color.TextPrimary))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.Surface))
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onEdit() }, containerColor = colorResource(id = R.color.Primary), shape = CircleShape) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = colorResource(id = R.color.white))
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.white)),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colorResource(id = R.color.black))
                    Spacer(modifier = Modifier.height(8.dp))

                    val priceText = try { currencyFormatter.format(item.price) } catch (_: Exception) { "$ ${item.price}" }
                    val total = item.price * item.quantity

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Precio Unidad:", fontSize = 14.sp, color = colorResource(id = R.color.TextSecondary))
                        Text(priceText, fontSize = 14.sp, color = colorResource(id = R.color.Primary), fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cantidad Disponible:", color = colorResource(id = R.color.TextSecondary))
                        Text(item.quantity.toString(), color = colorResource(id = R.color.black))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total:", color = colorResource(id = R.color.TextSecondary), fontWeight = FontWeight.Bold)
                        Text(try { currencyFormatter.format(total) } catch (_: Exception) { "$ $total" }, color = colorResource(id = R.color.Primary), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { onDelete() }, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.Secondary))) {
                Text("Eliminar", color = colorResource(id = R.color.TextPrimary), fontWeight = FontWeight.SemiBold)
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { onDismissDelete() },
                    title = { Text("Confirmar eliminación", fontWeight = FontWeight.Bold) },
                    text = { Text("¿Estás seguro de que deseas eliminar este producto?") },
                    confirmButton = {
                        TextButton(onClick = { onConfirmDelete() }) {
                            Text("Sí", color = colorResource(id = R.color.Primary), fontWeight = FontWeight.SemiBold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { onDismissDelete() }) {
                            Text("No", color = colorResource(id = R.color.TextSecondary))
                        }
                    }
                )
            }
        }
    }
}
