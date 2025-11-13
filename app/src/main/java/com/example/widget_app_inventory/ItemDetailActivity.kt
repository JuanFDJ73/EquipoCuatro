package com.example.widget_app_inventory

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.unit.sp
import com.example.widget_app_inventory.data.InventoryRepository
import com.example.widget_app_inventory.model.Item
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class ItemDetailActivity : ComponentActivity() {

    private val repo by lazy { InventoryRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val itemId = intent?.getLongExtra("itemId", -1L) ?: -1L
        setContent {
            ItemDetailApp(itemId)
        }
    }

    @Composable
    fun ItemDetailApp(itemId: Long) {
        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = colorResource(id = R.color.Background)
            ) {
                ItemDetailScreen(itemId)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ItemDetailScreen(itemId: Long) {
        var showDialog by remember { mutableStateOf(false) }
        val itemState = produceState<Item?>(initialValue = null, itemId) {
            value = repo.getItem(itemId)
        }
        val item = itemState.value
        val coroutineScope = rememberCoroutineScope()
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Detalle del producto",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = colorResource(id = R.color.TextPrimary)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            startActivity(Intent(this@ItemDetailActivity, InventoryListActivity::class.java))
                            finish()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
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
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        startActivity(
                            Intent(this@ItemDetailActivity, EditArticleActivity::class.java)
                                .putExtra("itemId", itemId)
                        )
                    },
                    containerColor = colorResource(id = R.color.Primary),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar",
                        tint = colorResource(id = R.color.white)
                    )
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
                        Text(
                            text = item?.let { it.name } ?: "Cargando...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(id = R.color.black)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val priceText = item?.let { try { currencyFormatter.format(it.price) } catch (_: Exception) { "$ ${it.price}" } } ?: "-"
                        val total = item?.let { it.price * it.quantity } ?: 0.0

                        // --- Fila Precio ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Precio Unidad:", fontSize = 14.sp, color = colorResource(id = R.color.TextSecondary))
                            Text(priceText, fontSize = 14.sp, color = colorResource(id = R.color.Primary), fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // --- Fila Cantidad ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Cantidad Disponible:", color = colorResource(id = R.color.TextSecondary))
                            Text(item?.quantity?.toString() ?: "-", color = colorResource(id = R.color.black))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // --- Fila Total ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total:", color = colorResource(id = R.color.TextSecondary), fontWeight = FontWeight.Bold)
                            Text(
                                try { currencyFormatter.format(total) } catch (_: Exception) { "$ $total" },
                                color = colorResource(id = R.color.Primary),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.Secondary))
                ) {
                    Text("Eliminar", color = colorResource(id = R.color.TextPrimary), fontWeight = FontWeight.SemiBold)
                }

                // Diálogo de confirmación
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Confirmar eliminación", fontWeight = FontWeight.Bold) },
                        text = { Text("¿Estás seguro de que deseas eliminar este producto?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDialog = false
                                    coroutineScope.launch {
                                        val deleted = repo.deleteItem(itemId)
                                        if (deleted) {
                                            // Actualizar widgets tras la eliminación
                                            try {
                                                val mgr = android.appwidget.AppWidgetManager.getInstance(this@ItemDetailActivity)
                                                val ids = mgr.getAppWidgetIds(android.content.ComponentName(this@ItemDetailActivity, InventoryWidgetProvider::class.java))
                                                val update = Intent(this@ItemDetailActivity, InventoryWidgetProvider::class.java).apply {
                                                    action = android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
                                                    putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                                                }
                                                sendBroadcast(update)
                                            } catch (t: Throwable) {
                                                // ignore
                                            }

                                            startActivity(Intent(this@ItemDetailActivity, InventoryListActivity::class.java))
                                            finish()
                                        }
                                    }
                                }
                            ) {
                                Text("Sí", color = colorResource(id = R.color.Primary), fontWeight = FontWeight.SemiBold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("No", color = colorResource(id = R.color.TextSecondary))
                            }
                        }
                    )
                }
            }
        }
    }
}