package com.example.widget_app_inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.widget_app_inventory.data.*
import com.example.widget_app_inventory.model.Item
import com.example.widget_app_inventory.viewmodel.InventoryViewModel
import java.text.NumberFormat
import java.util.Locale



class InventoryListFragment : Fragment() {

    private lateinit var vm: InventoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializa el ViewModel asociado a este Fragment
        vm = ViewModelProvider(this).get(InventoryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Crea una ComposeView para poder usar Jetpack Compose dentro de un Fragment
        return ComposeView(requireContext()).apply {
            setContent {
                // Llama al Composable principal, que ahora está definido abajo
                InventoryListScreen(vm = vm)
            }
        }
    }
}

// --- MUEVE TUS COMPOSABLES Y FUNCIONES DE AYUDA AQUÍ ---

@Composable
fun InventoryListScreen(vm: InventoryViewModel) {
    // Observa los cambios en la lista de productos desde el ViewModel
    val products by vm.items.collectAsState(initial = emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Implementar navegación para añadir producto */ }) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir producto")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Itera sobre la lista de productos y crea un ProductItem para cada uno
            items(products) { product ->
                ProductItem(product = product)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ProductItem(product: Item) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "Cantidad: ${product.quantity}", fontSize = 14.sp)
            }
            Text(text = formatPrice(product.price), fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

/**
 * Función de ayuda para formatear un Double a una cadena de texto de moneda (USD).
 */
private fun formatPrice(price: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(price)
}
