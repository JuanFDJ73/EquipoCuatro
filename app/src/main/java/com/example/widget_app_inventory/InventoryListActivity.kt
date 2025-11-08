package com.example.widget_app_inventory

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.widget_app_inventory.viewmodel.InventoryViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.foundation.clickable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class InventoryListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm = ViewModelProvider(this)[InventoryViewModel::class.java]
        setContent {
            InventoryListScreen(vm)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun InventoryListScreen(vm: InventoryViewModel) {
    val total by vm.total.collectAsState()
    val show by vm.showBalance.collectAsState()
    val items by vm.items.collectAsState()

    //Background
    Surface(modifier = Modifier.fillMaxSize(), color = colorResource(id = R.color.Background)) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = { Text(text = "Inventario", color = colorResource(id = R.color.TextPrimary)) },
                        actions = {
                            IconButton(
                                onClick = {
                                    // navigate back to login (logout)
                                    startActivity(Intent(this@InventoryListActivity, LoginActivity::class.java))
                                    finish()
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_logout),
                                    contentDescription = "Cerrar sesión",
                                    tint = colorResource(id = R.color.TextPrimary)
                                )
                            }

                        },
                        // Toolbar
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.Surface))
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            startActivity(Intent(this@InventoryListActivity, AddProductActivity::class.java))
                        },
                        containerColor = colorResource(id = R.color.Primary)
                        ) {
                        Text(
                            text = "+",
                            color = colorResource(id = R.color.Surface),
                            fontSize = 20.sp
                        )
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        // Text(text = "Inventory", style = MaterialTheme.typography.headlineSmall, color = colorResource(id = R.color.TextPrimary))
                        // Text(text = if (show) "Saldo: $${String.format("%,.2f", total)}" else "Saldo: $ ****", color = colorResource(id = R.color.TextPrimary))
                        // Button(onClick = { vm.toggleShow() }) { Text(text = if (show) "Ocultar" else "Mostrar") }
                        // Button(onClick = { vm.refresh() }) { Text(text = "Refrescar") }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 8.dp)
                        ) {
                            items(items) { item ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clickable {
                                            val intent = Intent(this@InventoryListActivity, ItemDetailActivity::class.java)
                                            intent.putExtra("itemId", item.id)
                                            startActivity(intent)
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.fillMaxWidth(0.7f)) {
                                            Text(text = item.name, color = colorResource(id = R.color.black), style = MaterialTheme.typography.titleMedium)
                                            Text(text = "Id: ${item.id}", color = colorResource(id = R.color.TextSecondary), style = MaterialTheme.typography.bodySmall)
                                        }
                                        Text(text = formatPrice(item.price), color = colorResource(id = R.color.Primary), style = MaterialTheme.typography.titleSmall)
                                    }
                                }
                            }
                        }

                    }
                }
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
