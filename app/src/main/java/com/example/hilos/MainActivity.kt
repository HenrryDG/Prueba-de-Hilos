package com.example.hilos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.coroutines.*
import kotlin.concurrent.thread
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var orderStatusTextView: TextView
    private lateinit var inventoryTextView: TextView
    private lateinit var placeOrderButton: Button
    private lateinit var checkInventoryButton: Button
    private lateinit var processPaymentButton: Button
    private lateinit var progressBar: ProgressBar

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job()) // El Distpatcher.Main nos permite ejecutar la corrutina en el hilo principal
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
        if (inventoryCount > 0) {
            Thread {
                runOnUiThread {
                    orderStatusTextView.text = "Colocando orden..."
                    progressBar.visibility = ProgressBar.VISIBLE
                }

                // Simulamos la colocación de la orden en segundo plano
                Thread.sleep(3000) // Simula un proceso que toma 3 segundos
                val random = Random.nextInt(1000, 9999)
                val orderResult = "Orden #$random colocada exitosamente"

                // Actualizamos el inventario después de colocar la orden
                inventoryCount--
                updateInventory()

                runOnUiThread {
                    orderPlaced = true
                    orderStatusTextView.text = orderResult
                    progressBar.visibility = ProgressBar.INVISIBLE
                }
            }.start()
        } else {
            orderStatusTextView.text = "No hay productos disponibles para ordenar"
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
        if (!orderPlaced) {
            // Mostrar una alerta si no se ha colocado una orden, usando un Toast
            Toast.makeText(this@MainActivity, "Primero debes colocar una orden", Toast.LENGTH_SHORT).show()
        } else {
            Thread {
                runOnUiThread {
                    orderStatusTextView.text = "Procesando pago..."
                    progressBar.visibility = ProgressBar.VISIBLE
                }

                // Simulamos el procesamiento de pago en segundo plano
                Thread.sleep(4000) // Simula un proceso que toma 4 segundos
                val paymentResult = if (Random.nextBoolean()) "Pago procesado exitosamente" else "Error en el procesamiento del pago"

                runOnUiThread {
                    orderStatusTextView.text = paymentResult
                    orderPlaced = false
                    progressBar.visibility = ProgressBar.INVISIBLE
                }

                // Esperamos 2 segundos en segundo plano antes de actualizar el estado de la orden
                Thread.sleep(2000)

                runOnUiThread {
                    orderStatusTextView.text = "Esperando orden..."
                }
            }.start()
        }
    }


    private fun updateInventory() {
        Thread {
            runOnUiThread {
                inventoryTextView.text = "Actualizando inventario..."
                progressBar.visibility = ProgressBar.VISIBLE
            }

            Thread.sleep(2000) // Simula un proceso que toma 2 segundos
            val inventoryStatus = "Productos disponibles: $inventoryCount"

            runOnUiThread {
                inventoryTextView.text = inventoryStatus
                progressBar.visibility = ProgressBar.INVISIBLE
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Cancelamos todas las corrutinas al destruir la actividad
    }
}