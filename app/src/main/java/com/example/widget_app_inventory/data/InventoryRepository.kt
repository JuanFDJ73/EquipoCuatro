package com.example.widget_app_inventory.data

import android.content.Context
import com.example.widget_app_inventory.model.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class InventoryRepository(private val context: Context) {

    private val db by lazy { AppDatabase.getInstance(context) }
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getItems(): List<Item> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                // Si no hay usuario, retornar lista vacía
                return emptyList()
            }

            val userId = currentUser.uid
            val snapshot = firestore
                .collection("usuarios")
                .document(userId)
                .collection("IdProductos")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    val codigo = doc.getString("codigo") ?: return@mapNotNull null
                    val nombre = doc.getString("nombre") ?: return@mapNotNull null
                    val precio = doc.getDouble("precio") ?: 0.0
                    val cantidad = doc.getLong("cantidad")?.toInt() ?: 0

                    Item(
                        id = 0, // Firestore docs no tienen auto-increment, usamos 0
                        codigo = codigo,
                        name = nombre,
                        price = precio,
                        quantity = cantidad
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("InventoryRepository", "Error cargando items de Firestore", e)
            emptyList()
        }
    }

    suspend fun insertItem(item: Item): Long {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return -1L
            }

            val userId = currentUser.uid
            val productData = hashMapOf(
                "codigo" to item.codigo,
                "nombre" to item.name,
                "precio" to item.price,
                "cantidad" to item.quantity,
                "timestamp" to com.google.firebase.Timestamp.now()
            )

            firestore
                .collection("usuarios")
                .document(userId)
                .collection("IdProductos")
                .document(item.codigo)
                .set(productData)
                .await()

            // También guardar en Room para compatibilidad local
            db.itemDao().insert(item)
        } catch (e: Exception) {
            android.util.Log.e("InventoryRepository", "Error insertando item", e)
            -1L
        }
    }

    suspend fun getItem(id: Long): Item? {
        return try {
            db.itemDao().getById(id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getItemByCode(codigo: String): Item? {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) return null

            val userId = currentUser.uid
            val snapshot = firestore
                .collection("usuarios")
                .document(userId)
                .collection("IdProductos")
                .document(codigo)
                .get()
                .await()

            if (snapshot.exists()) {
                val nombre = snapshot.getString("nombre") ?: return null
                val precio = snapshot.getDouble("precio") ?: 0.0
                val cantidad = snapshot.getLong("cantidad")?.toInt() ?: 0

                Item(
                    id = 0,
                    codigo = codigo,
                    name = nombre,
                    price = precio,
                    quantity = cantidad
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateItem(item: Item): Boolean {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) return false

            val userId = currentUser.uid
            val updateData: Map<String, Any> = mapOf(
                "nombre" to item.name,
                "precio" to item.price,
                "cantidad" to item.quantity,
                "timestamp" to com.google.firebase.Timestamp.now()
            )

            firestore
                .collection("usuarios")
                .document(userId)
                .collection("IdProductos")
                .document(item.codigo)
                .update(updateData)
                .await()

            // Si Firestore se actualiza exitosamente, retornar true
            try {
                val roomItem = db.itemDao().getAll().find { it.codigo == item.codigo }
                if (roomItem != null) {
                    db.itemDao().update(roomItem.copy(name = item.name, price = item.price, quantity = item.quantity))
                }
            } catch (e: Exception) {
                android.util.Log.d("InventoryRepository", "Error actualizando en Room, pero Firestore OK: ${e.message}")
            }
            true
        } catch (e: Exception) {
            android.util.Log.e("InventoryRepository", "Error actualizando item en Firestore", e)
            false
        }
    }

    suspend fun deleteItem(id: Long): Boolean {
        return try {
            val item = db.itemDao().getById(id) ?: return false
            deleteItemByCode(item.codigo)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteItemByCode(codigo: String): Boolean {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) return false

            val userId = currentUser.uid
            firestore
                .collection("usuarios")
                .document(userId)
                .collection("IdProductos")
                .document(codigo)
                .delete()
                .await()

            // También eliminar de Room
            val item = db.itemDao().getAll().find { it.codigo == codigo }
            if (item != null) {
                db.itemDao().deleteById(item.id) > 0
            }
            true
        } catch (e: Exception) {
            android.util.Log.e("InventoryRepository", "Error eliminando item", e)
            false
        }
    }

    fun computeTotal(items: List<Item>): Double = items.sumOf { it.price * it.quantity }
}