package com.example.hilos

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.ProgressBar
import android.widget.Toast
import kotlin.random.Random

@SuppressLint("SetTextI18n") // Para evitar advertencias de internacionalización

class MainActivity : AppCompatActivity() {
    private lateinit var orderStatusTextView: TextView
    private lateinit var inventoryTextView: TextView
    private lateinit var placeOrderButton: Button
    private lateinit var checkInventoryButton: Button
    private lateinit var processPaymentButton: Button
    private lateinit var progressBar: ProgressBar

    private var orderPlaced: Boolean = false
    private var inventoryCount: Int = 50

    private val handler = Handler(Looper.getMainLooper()) { message ->
        when (message.what) {
            1 -> showNotification("Pago exitoso")  // Para pago exitoso
            2 -> showNotification("Pago fallido")  // Para pago fallido
        }
        true
    }


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
                runOnUiThread { // runOnUiThread nos permite ejecutar código en el hilo principal
                    orderStatusTextView.text = "Colocando orden..."
                    progressBar.visibility = ProgressBar.VISIBLE
                }

                // Simulamos la colocación de la orden en segundo plano
                Thread.sleep(3000) // Simula un proceso que toma 3 segundos
                val random = Random.nextInt(1000, 9999)
                val orderResult = "Orden #$random colocada exitosamente"

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
        Thread {
            runOnUiThread {
                inventoryTextView.text = "Verificando inventario..."
                progressBar.visibility = ProgressBar.VISIBLE
            }

            // Simulamos la verificación del inventario en segundo plano
            Thread.sleep(2000) // Simula un proceso que toma 2 segundos
            val inventoryStatus = "Productos disponibles: $inventoryCount"

            runOnUiThread {
                inventoryTextView.text = inventoryStatus
                progressBar.visibility = ProgressBar.INVISIBLE
            }
        }.start()
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
                val paymentSuccess = Random.nextBoolean()
                val paymentResult = if (paymentSuccess){
                    "Pago exitoso"
                } else {
                    "Pago fallido por favor intente de nuevo"
                }

                if(paymentSuccess){
                    updateInventory()
                    handler.sendEmptyMessage(1)

                }
                else{
                    handler.sendEmptyMessage(2)
                }

                orderPlaced = false

                runOnUiThread {
                    orderStatusTextView.text = paymentResult
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

            inventoryCount--
            Thread.sleep(2000) // Simula un proceso que toma 2 segundos
            val inventoryStatus = "Productos disponibles: $inventoryCount"

            runOnUiThread {
                inventoryTextView.text = inventoryStatus
                progressBar.visibility = ProgressBar.INVISIBLE
            }
        }.start()
    }

    private fun showNotification(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
    }
}