package com.example.widget_app_inventory.ui.addproduct

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.widget_app_inventory.ui.inventorylist.InventoryListActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddProductActivity : ComponentActivity() {

    private val viewModel: AddProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AddProductScreen(
                onBack = {
                    startActivity(Intent(this, InventoryListActivity::class.java))
                    finish()
                },
                onSave = { codigo, nombre, precio, cantidad ->

                    val codeLong = codigo.toLongOrNull() ?: 0L
                    val priceDouble = precio.toDoubleOrNull() ?: 0.0
                    val qty = cantidad.toIntOrNull() ?: 0

                    val item = com.example.widget_app_inventory.model.Item(
                        id = codeLong,
                        name = nombre,
                        price = priceDouble,
                        quantity = qty
                    )

                    lifecycleScope.launch {
                        if (codeLong > 0 && viewModel.isCodeExists(codeLong)) {
                            Toast.makeText(
                                this@AddProductActivity,
                                "El código ya existe.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            viewModel.insertItem(item) {
                                startActivity(
                                    Intent(
                                        this@AddProductActivity,
                                        InventoryListActivity::class.java
                                    )
                                )
                                finish()
                            }
                        }
                    }
                }
            )
        }
    }
}
