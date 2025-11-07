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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }

    @Composable
    fun MainScreen() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "MainActivity", style = MaterialTheme.typography.headlineSmall)

            Button(onClick = { startActivity(Intent(this@MainActivity, LoginActivity::class.java)) }) {
                Text(text = "Login")
            }

            Button(onClick = { startActivity(Intent(this@MainActivity, InventoryListActivity::class.java)) }) {
                Text(text = "Inventory List")
            }

            Button(onClick = { startActivity(Intent(this@MainActivity, ItemDetailActivity::class.java)) }) {
                Text(text = "Item Detail")
            }

            Button(onClick = { startActivity(Intent(this@MainActivity, ManageInventoryActivity::class.java)) }) {
                Text(text = "Manage Inventory")
            }

            Button(onClick = { startActivity(Intent(this@MainActivity, SettingsActivity::class.java)) }) {
                Text(text = "Settings")
            }
        }
    }
}