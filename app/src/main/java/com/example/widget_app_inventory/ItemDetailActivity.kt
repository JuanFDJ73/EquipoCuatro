package com.example.widget_app_inventory

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ItemDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val itemId = intent?.getLongExtra("itemId", -1L) ?: -1L
        setContent {
            ItemDetailApp()
        }
    }

    @Composable
    fun ItemDetailApp() {
        MaterialTheme(
            colorScheme = lightColorScheme(
                primary = Color(0xFFFF9800),
                secondary = Color(0xFFE53935),
                background = Color(0xFFF8F9FA)
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val itemId = intent?.getLongExtra("itemId", -1L) ?: -1L
                setContent {
                    ItemDetailScreen(itemId)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ItemDetailScreen(itemId: Long) {
        var showDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Detalle del producto",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = Color(0xFF222222)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { startActivity(Intent(this@ItemDetailActivity, InventoryListActivity::class.java)); finish() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color(0xFF555555)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFE5E5E5)
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { /* Acción editar */ },
                    containerColor = Color(0xFFFF9800),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar",
                        tint = Color.White
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End
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
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = "ItemId: $itemId", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Precio: $$itemId", color = Color.Gray)
                        Text("Cantidad: x$itemId", color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Total: $$itemId",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text("Eliminar", color = Color.White, fontWeight = FontWeight.SemiBold)
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("¿Sí / No?", fontWeight = FontWeight.Medium) },
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Sí", color = Color(0xFFE53935))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("No", color = Color.Gray)
                            }
                        }
                    )
                }
            }
        }

    }
}


/*
Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "ItemId: $itemId", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Precio: $$itemId", color = Color.Gray)
            Text("Cantidad: x$itemId", color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Total: $$itemId",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )


        }














Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Item Detail (placeholder)", style = MaterialTheme.typography.headlineSmall)
            Text(text = "ItemId: $itemId")
            Button(onClick = {
                val i = Intent(this@ItemDetailActivity, EditArticleActivity::class.java)
                i.putExtra("itemId", itemId)
                startActivity(i)
            }) { Text(text = "Editar artículo") }
 */