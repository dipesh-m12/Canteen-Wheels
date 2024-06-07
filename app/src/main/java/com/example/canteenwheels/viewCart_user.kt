package com.example.canteenwheels

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.canteenwheels.databinding.ActivityCartUserBinding
import com.example.canteenwheels.databinding.ActivityViewCartUserBinding
import com.squareup.picasso.Picasso

class viewCart_user : AppCompatActivity() {
    private lateinit var binding:ActivityViewCartUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityViewCartUserBinding.inflate(layoutInflater)
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
//        Log.d("viewCart_user", "id: $id, username: $username, item: $item, price: $price, quantity: $quantity, time: $time, total: $total, type: $type, image: $image, status: $status, completed: $completed")

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
}