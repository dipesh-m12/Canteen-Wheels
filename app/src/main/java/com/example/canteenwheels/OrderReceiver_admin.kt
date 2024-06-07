package com.example.canteenwheels

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.canteenwheels.databinding.ActivityOrderReceiverAdminBinding
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrderReceiver_admin : AppCompatActivity(), OnItemClickListener1 {
    private var currentToast: Toast? = null
    private lateinit var binding: ActivityOrderReceiverAdminBinding
    private lateinit var adapter: MyAdapter_orderreceiver_admin
    private val cartItems = mutableListOf<CartItem>()
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderReceiverAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.additem.setOnClickListener {
            val intent = Intent(this@OrderReceiver_admin, Admin_Config::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        fetchDataFromFirestore()
    }

    private fun setupRecyclerView() {
        adapter = MyAdapter_orderreceiver_admin(this, cartItems, this)
        binding.recyclerOrderreceiver.layoutManager = LinearLayoutManager(this)
        binding.recyclerOrderreceiver.adapter = adapter
        binding.recyclerOrderreceiver.itemAnimator = DefaultItemAnimator()
    }

    private fun fetchDataFromFirestore() {
        binding.progressBar.visibility = View.VISIBLE
        db.collection("order_details")
            .orderBy("time", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                cartItems.clear()
                for (document in documents) {
                    val id = document.id
                    val username = document.getString("username") ?: ""
                    val item = document.getString("item") ?: ""
                    val price = (document.getLong("price") ?: 0).toString()
                    val quantity = (document.getLong("quantity") ?: 0).toString()
                    val time = document.getDate("time").toString()
                    val total = (document.getLong("total") ?: 0).toString()
                    val type = document.getString("type") ?: ""
                    val image = document.getString("image") ?: ""
                    val status = document.getString("status") ?: ""
                    val completed = (document.getBoolean("completed") ?: false).toString()

                    val cartItem = CartItem(id, username, item, price, quantity, time, total, type, image, status, completed)
                    cartItems.add(cartItem)
                }
                binding.progressBar.visibility = View.GONE
                if (cartItems.isEmpty()) {
                    showToast("No orders!")
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("cartuser", "$exception")
                showToast("Failed to fetch orders")
                binding.progressBar.visibility = View.GONE
            }
    }

    private fun showToast(message: String) {
        currentToast?.cancel()
        currentToast = Toast.makeText(this@OrderReceiver_admin, message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }

    override fun onItemClick(item: CartItem) {
        val intent = Intent(this, admin_orderConfirmation::class.java)
        intent.putExtra("id", item.id)
        intent.putExtra("username", item.username)
        intent.putExtra("item", item.item)
        intent.putExtra("price", item.price)
        intent.putExtra("quantity", item.quantity)
        intent.putExtra("time", item.time)
        intent.putExtra("total", item.total)
        intent.putExtra("type", item.type)
        intent.putExtra("image", item.image)
        intent.putExtra("status", item.status)
        intent.putExtra("completed", item.completed)
        startActivity(intent)
    }
}
