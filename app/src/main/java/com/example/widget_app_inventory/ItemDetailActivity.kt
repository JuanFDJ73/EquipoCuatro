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

class ItemDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val itemId = intent?.getLongExtra("itemId", -1L) ?: -1L
        setContent {
            ItemDetailScreen(itemId)
        }
    }

    @Composable
    fun ItemDetailScreen(itemId: Long) {
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
        }
    }
}
