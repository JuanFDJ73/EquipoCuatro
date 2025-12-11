package com.example.widget_app_inventory.ui.inventorylist

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.widget_app_inventory.R
import com.example.widget_app_inventory.model.Item
import com.example.widget_app_inventory.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InventoryListActivity : ComponentActivity() {

    private val viewModel: InventoryViewModel by viewModels()
    private val sessionPrefs by lazy { getSharedPreferences("session_prefs", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val logged = sessionPrefs.getBoolean("is_logged_in", false)
        if (!logged) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContent {
            InventoryListScreen(viewModel)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun InventoryListScreen(vm: InventoryViewModel) {
        val total by vm.total.collectAsState()
        val show by vm.showBalance.collectAsState()
        val items by vm.items.collectAsState()
        val isLoading by vm.isLoading.collectAsState()

        Surface(modifier = Modifier.fillMaxSize(), color = colorResource(id = R.color.Background)) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = { Text(text = "Inventario", color = colorResource(id = R.color.TextPrimary)) },
                        actions = {
                            IconButton(onClick = {
                                sessionPrefs.edit().putBoolean("is_logged_in", false).apply()
                                startActivity(Intent(this@InventoryListActivity, LoginActivity::class.java))
                                finish()
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_logout),
                                    contentDescription = "Cerrar sesión",
                                    tint = colorResource(id = R.color.TextPrimary)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.Surface))
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { startActivity(Intent(this@InventoryListActivity, com.example.widget_app_inventory.ui.addproduct.AddProductActivity::class.java)) },
                        containerColor = colorResource(id = R.color.Primary)
                    ) {
                        Text("+", color = colorResource(id = R.color.Surface), fontSize = 20.sp)
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                            items(items) { item ->
                                InventoryItemCard(item)
                            }
                        }
                    }

                    if (isLoading) {
                        CircularProgressIndicator(
                            color = colorResource(id = R.color.Primary),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun InventoryItemCard(item: Item) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { /* Abrir detalle del item */ },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.fillMaxWidth(0.7f)) {
                    Text(item.name, color = colorResource(id = R.color.black), style = MaterialTheme.typography.titleMedium)
                    Text("Id: ${item.id}", color = colorResource(id = R.color.TextSecondary), style = MaterialTheme.typography.bodySmall)
                }
                Text(formatPrice(item.price), color = colorResource(id = R.color.Primary), style = MaterialTheme.typography.titleSmall)
            }
        }
    }

    private fun formatPrice(amount: Double): String {
        val symbols = java.text.DecimalFormatSymbols().apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val df = java.text.DecimalFormat("#,##0.00", symbols)
        return "$ ${df.format(amount)}"
    }
}