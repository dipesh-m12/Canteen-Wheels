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
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.canteenwheels.databinding.ActivityCartUserBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

interface OnItemClickListener1 {
    fun onItemClick(item: CartItem)
}

class cart_user : AppCompatActivity(),OnItemClickListener1  {
    private var currentToast: Toast? = null
    private lateinit var binding: ActivityCartUserBinding
    private lateinit var adapter: MyAdapter_cart_user
    private val cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartUserBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
        val useremail = intent.getStringExtra("useremail")

        val db = Firebase.firestore

        db.collection("order_details")
            .orderBy("time", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                binding.progressBarCart.visibility= View.VISIBLE
                for (document in documents) {

                    val id = document.id
                    val username = document.getString("username") ?: ""
                    if(useremail==username){
                        val item = document.getString("item") ?: ""
                        val price = (document.getLong("price") ?: 0).toString()
                        val quantity = (document.getLong("quantity") ?: 0).toString()
                        val time = document.getDate("time").toString()
                        val total = (document.getLong("total") ?: 0).toString()
                        val type = document.getString("type") ?: ""
                        val image = document.getString("image") ?: ""
                        val status = document.getString("status") ?: ""
                        val completed = (document.getBoolean("completed") ?: "").toString()

                        val cartItem = CartItem(id, username, item, price, quantity, time, total, type, image, status, completed)

                        cartItems.add(cartItem)
                    }
                    binding.progressBarCart.visibility= View.GONE
                }
                if(cartItems.isEmpty()) {
                    showToast("Empty cart")
                    binding.progressBarCart.visibility= View.GONE
                    return@addOnSuccessListener             //can produce bugs
                }

                adapter = MyAdapter_cart_user(this, cartItems, this )
                binding.cartUserRecycler.layoutManager = LinearLayoutManager(this)
                binding.cartUserRecycler.adapter = adapter
                binding.cartUserRecycler.itemAnimator = DefaultItemAnimator()

            }
            .addOnFailureListener { exception ->
                Log.d("cartuser","$exception")
                Toast.makeText(this@cart_user,"Failed to fetch your orders",Toast.LENGTH_SHORT).show()
            }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
    private fun showToast(message: String) {
        currentToast?.cancel()
        currentToast = Toast.makeText(this@cart_user, message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }
    override fun onItemClick(item: CartItem) {
        val intent = Intent(this, viewCart_user::class.java)
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
//        Log.d("cart_user", "id: ${item.id}, username: ${item.username}, item: ${item.item}, price: ${item.price}, quantity: ${item.quantity}, time: ${item.time}, total: ${item.total}, type: ${item.type}, image: ${item.image}, status: ${item.status}, completed: ${item.completed}")
        startActivity(intent)
    }
}