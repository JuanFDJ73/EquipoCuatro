package com.example.widget_app_inventory

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.widget_app_inventory.data.InventoryRepository
import com.example.widget_app_inventory.ui.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

// Clase principal que controla el comportamiento del widget en la pantalla de inicio
class InventoryWidgetProvider : AppWidgetProvider() {

    companion object {
        private const val ACTION_TOGGLE = "com.example.widget_app_inventory.ACTION_TOGGLE_SHOW"
        private const val EXTRA_WIDGET_ID = "extra_widget_id"
        private const val PREFS_NAME = "widget_prefs"
        private const val PREF_SHOW_PREFIX = "show_"
    }

    // Se ejecuta cuando el sistema pide actualizar uno o varios widgets
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (appWidgetId in appWidgetIds) {
            // Actualiza cada widget individualmente
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    // Se ejecuta cuando el widget recibe un broadcast (por ejemplo, un clic en el ojo)
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action

        // Verifica si la acción recibida es la de alternar mostrar/ocultar saldo
        if (action == ACTION_TOGGLE) {
            val id = intent.getIntExtra(EXTRA_WIDGET_ID, -1)
            if (id != -1) {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val key = PREF_SHOW_PREFIX + id

                // Cambia el estado actual (si estaba oculto, lo muestra; si estaba visible, lo oculta)
                val current = prefs.getBoolean(key, false)
                prefs.edit().putBoolean(key, !current).apply()

                // Actualiza visualmente el widget después del cambio
                val appWidgetManager = AppWidgetManager.getInstance(context)
                updateAppWidget(context, appWidgetManager, id)
            }
        }
    }

    // Actualiza el contenido visual y los datos del widget
    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_base)

        // Establece los íconos del logo principal y del engranaje (por si acaso)
        views.setImageViewResource(R.id.widgetLogo, R.drawable.ic_widget)
        views.setImageViewResource(R.id.widgetManageIcon, R.drawable.settings)

        // Configuracion del boton "ojo"
        // Crea un intent para alternar entre mostrar/ocultar el valor del inventario
        val toggleIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = ACTION_TOGGLE
            putExtra(EXTRA_WIDGET_ID, appWidgetId)
        }

        // Crea el PendingIntent que ejecutará el toggle cuando se toque el ícono del ojo
        val togglePending = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widgetEye, togglePending)

        // Configuracion del boton de gestion (engranaje)
        // Crea un intent para abrir la LoginActivity al tocar el engranaje
        val manageIntent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        // PendingIntent para abrir la actividad de gestión
        val managePending = PendingIntent.getActivity(
            context,
            appWidgetId + 1000,
            manageIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widgetManageIcon, managePending)

        // Lee la referencia de mostrar/ocultar saldo
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val show = prefs.getBoolean(PREF_SHOW_PREFIX + appWidgetId, false)

        // Cargar los datos desde la base de datos en segundo plano
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = InventoryRepository(context.applicationContext)
                val items = repo.getItems()                 // Obtiene todos los productos
                val total = repo.computeTotal(items)        // Calcula el valor total del inventario

                val formatted = formatCurrency(total)       // Formatea el número en formato moneda

                if (show) {
                    // Si la preferencia está en "mostrar", enseña el valor real
                    views.setTextViewText(R.id.widgetBalance, "$ $formatted")
                    views.setImageViewResource(R.id.widgetEye, R.drawable.ic_hide)
                } else {
                    // Si está en "ocultar", muestra asteriscos
                    views.setTextViewText(R.id.widgetBalance, "$ ****")
                    views.setImageViewResource(R.id.widgetEye, R.drawable.ic_view)
                }

                // Aplica los cambios visuales al widget
                appWidgetManager.updateAppWidget(appWidgetId, views)

            } catch (e: Exception) {
                // Si ocurre un error (por ejemplo, DB inaccesible), muestra asteriscos por defecto
                views.setTextViewText(R.id.widgetBalance, "$ ****")
                views.setImageViewResource(R.id.widgetEye, R.drawable.ic_view)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    // Función auxiliar que da formato a los valores monetarios (usa punto y coma según configuración local)
    private fun formatCurrency(amount: Double): String {
        val symbols = DecimalFormatSymbols().apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val df = DecimalFormat("#,##0.00", symbols)
        return df.format(amount)
    }
}