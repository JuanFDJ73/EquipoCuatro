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

class EditArticleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val itemId = intent?.getIntExtra("itemId", -1) ?: -1
        setContent {
            EditArticleScreen(itemId)
        }
    }

    @Composable
    fun EditArticleScreen(itemId: Int) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Edit Article (placeholder) for itemId=$itemId", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = { startActivity(Intent(this@EditArticleActivity, InventoryListActivity::class.java)); finish() }) {
                Text(text = "Back to Inventory")
            }
        }
    }
}
