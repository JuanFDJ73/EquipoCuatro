package com.example.widget_app_inventory

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.widget_app_inventory.viewmodel.InventoryViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

class InventoryListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm = ViewModelProvider(this)[InventoryViewModel::class.java]
        setContent {
            InventoryListScreen(vm)
        }
    }

    @Composable
    fun InventoryListScreen(vm: InventoryViewModel) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val total by vm.total.collectAsState()
            val show by vm.showBalance.collectAsState()

            Text(text = "Inventory List (MVVM)", style = MaterialTheme.typography.headlineSmall)
            Text(text = if (show) "Saldo: $${String.format("%,.2f", total)}" else "Saldo: $ ****")
            Button(onClick = { vm.toggleShow() }) { Text(text = if (show) "Ocultar" else "Mostrar") }
            Button(onClick = { vm.refresh() }) { Text(text = "Refrescar") }
            Button(onClick = { startActivity(Intent(this@InventoryListActivity, MainActivity::class.java)); finish() }) {
                Text(text = "Back to Main")
            }
        }
    }
}
