package com.example.hilos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.coroutines.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var orderStatusTextView: TextView
    private lateinit var inventoryTextView: TextView
    private lateinit var placeOrderButton: Button
    private lateinit var checkInventoryButton: Button
    private lateinit var processPaymentButton: Button
    private lateinit var progressBar: ProgressBar

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job()) // El Dispatcher Main es necesario para actualizar la UI y el Job para cancelar las corrutinas
    private var orderPlaced: Boolean = false
    private var inventoryCount: Int = 50

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        orderStatusTextView = findViewById(R.id.orderStatusTextView)
        inventoryTextView = findViewById(R.id.inventoryTextView)
        placeOrderButton = findViewById(R.id.placeOrderButton)
        checkInventoryButton = findViewById(R.id.checkInventoryButton)
        processPaymentButton = findViewById(R.id.processPaymentButton)
        progressBar = findViewById(R.id.progressBar)

        placeOrderButton.setOnClickListener { placeOrder() }
        checkInventoryButton.setOnClickListener { checkInventory() }
        processPaymentButton.setOnClickListener { processPayment() }
    }

    private fun placeOrder() {
        coroutineScope.launch {
            if (inventoryCount > 0)
            {
                orderStatusTextView.text = "Colocando orden..."
                progressBar.visibility = ProgressBar.VISIBLE

                // Simulamos la colocación de la orden en segundo plano
                val orderResult = withContext(Dispatchers.Default) {
                    delay(3000) // Simula un proceso que toma 3 segundos
                    // poner un numero random para simular una idea de si la orden fue exitosa o no
                    val random = Random.nextInt(1000, 9999)
                    "Orden #$random colocada exitosamente"

                }

                updateInventory()
                inventoryCount--
                orderPlaced = true
                orderStatusTextView.text = orderResult
            }
            else
            {
                orderStatusTextView.text = "No hay productos disponibles para ordenar"

            }
            progressBar.visibility = ProgressBar.INVISIBLE
        }
    }

    private fun checkInventory() {
        coroutineScope.launch {
            inventoryTextView.text = "Verificando inventario..."
            progressBar.visibility = ProgressBar.VISIBLE

            // Simulamos la verificación de inventario en segundo plano
            val inventoryStatus = withContext(Dispatchers.Default) {
                delay(2000) // Simula un proceso que toma 2 segundos
                "Productos disponibles: $inventoryCount"
            }

            inventoryTextView.text = inventoryStatus
            progressBar.visibility = ProgressBar.INVISIBLE
        }
    }

    private fun processPayment() {
        coroutineScope.launch {
            if (!orderPlaced) {
                // mostrar una alerta si no se ha colocado una orden que se meustre como un Toast
                Toast.makeText(this@MainActivity, "Primero debes colocar una orden", Toast.LENGTH_SHORT).show()
            }
            else{
                orderStatusTextView.text = "Procesando pago..."
                progressBar.visibility = ProgressBar.VISIBLE

                // Simulamos el procesamiento de pago en segundo plano
                val paymentResult = withContext(Dispatchers.Default) {
                    delay(4000) // Simula un proceso que toma 4 segundos
                    if (Random.nextBoolean()) "Pago procesado exitosamente" else "Error en el procesamiento del pago"
                }

                orderStatusTextView.text = paymentResult
            }
            orderPlaced = false
            delay(2000) // Simula un proceso que toma 2 segundos
            orderStatusTextView.text = "Esperando orden..."
            progressBar.visibility = ProgressBar.INVISIBLE
        }
    }

    private fun updateInventory()
    {

        coroutineScope.launch {
            inventoryTextView.text = "Actualizando inventario..."
            progressBar.visibility = ProgressBar.VISIBLE

            // Simulamos la actualización de inventario en segundo plano
            val inventoryStatus = withContext(Dispatchers.Default) {
                delay(2000) // Simula un proceso que toma 2 segundos
                "Productos disponibles: $inventoryCount"
            }

            inventoryTextView.text = inventoryStatus
            progressBar.visibility = ProgressBar.INVISIBLE
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Cancelamos todas las corrutinas al destruir la actividad
    }
}