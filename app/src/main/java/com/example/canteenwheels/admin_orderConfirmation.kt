package com.example.canteenwheels

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.canteenwheels.databinding.ActivityAdminOrderConfirmationBinding
import com.example.canteenwheels.databinding.ActivityViewCartUserBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso

class admin_orderConfirmation : AppCompatActivity() {
    private lateinit var binding: ActivityAdminOrderConfirmationBinding
    private var currentToast: Toast? = null
    private val firestoreDB = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityAdminOrderConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val id = intent.getStringExtra("id")
        val username = intent.getStringExtra("username")
        val item = intent.getStringExtra("item")
        val price = intent.getStringExtra("price")
        val quantity = intent.getStringExtra("quantity")
        val time = intent.getStringExtra("time")
        val total = intent.getStringExtra("total")
        val type = intent.getStringExtra("type")
        val image = intent.getStringExtra("image")
        val status = intent.getStringExtra("status")
        val completed = intent.getStringExtra("completed")

        if(status=="confirmed"){
            showToast("Order Already Confirmed!")
            binding.Confirmbtn.isEnabled=false
            binding.Confirmbtn.text="Confirmed!"
        }

        if(completed=="true"){
            showToast("Order Already completed!")
            binding.Completebtn.isEnabled=false
            binding.Completebtn.text="Completed!"
        }
        binding.Confirmbtn.setOnClickListener(){
            binding.progressBarAdminConfirm.visibility= View.VISIBLE
            id?.let { confirmOrder(it) }
        }
        binding.Completebtn.setOnClickListener(){
            binding.progressBarAdminConfirm.visibility= View.VISIBLE
            id?.let { completeOrder(it) }
        }




        binding.Orderid.text=id
        binding.username.text=username
        binding.item.text=item
        binding.price.text=price
        binding.quantity.text=quantity
        binding.time.text=time
        binding.total.text=total
        binding.type.text=type
        binding.status.text=status
        binding.completed.text=completed


        Picasso.get()
            .load(image)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(binding.image)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun completeOrder(orderId: String) {
        val orderRef = firestoreDB.collection("order_details").document(orderId)
        orderRef.update("completed", true)
            .addOnSuccessListener {
                showToast("Order Completed!")
                binding.Completebtn.isEnabled = false
                binding.Completebtn.text = "Completed!"
                binding.completed.text="true"
                binding.progressBarAdminConfirm.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                showToast("Failed to Complete Order: ${e.message}")
                binding.progressBarAdminConfirm.visibility = View.GONE
            }
    }

    private fun confirmOrder(orderId: String) {
        val orderRef = firestoreDB.collection("order_details").document(orderId)
        orderRef.update("status", "confirmed")
            .addOnSuccessListener {
                showToast("Order Confirmed!")
                binding.Confirmbtn.isEnabled = false
                binding.Confirmbtn.text = "Confirmed!"
                binding.status.text="confirmed"
                binding.progressBarAdminConfirm.visibility= View.GONE
            }
            .addOnFailureListener { e ->
                showToast("Failed to Confirm Order: ${e.message}")
                binding.progressBarAdminConfirm.visibility= View.GONE
            }
    }

    private fun showToast(message: String) {
        // Check if there's a current toast, cancel it before showing the new one
        currentToast?.cancel()
        // Show the new toast
        currentToast = Toast.makeText(this@admin_orderConfirmation, message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }
}