package com.example.canteenwheels

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.canteenwheels.databinding.ActivityViewMenuAdminBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class View_Menu_admin : AppCompatActivity() {
    private lateinit var binding: ActivityViewMenuAdminBinding
    private var currentToast: Toast? = null // Variable to hold the current toast
    private val foodItemList = mutableListOf<FoodItemData>()

    // Define debounce threshold for button clicks
//    private val debounceThreshold = 500L // milliseconds

    override fun onPause() {
        super.onPause()
        finish()

    }


    override fun onStart() {
        super.onStart()
        Log.d("view_admin", "onStart called")
        Toast.makeText(this,"Click for deleting menu items",Toast.LENGTH_LONG).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("view_admin", "onCreate called")
        enableEdgeToEdge()
        binding = ActivityViewMenuAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        val db = Firebase.firestore
//        val collectionRef = db.collection("food_details")

        fetchDataFromFirestore()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        disableButtons()
//        enableButtonsAfterDelay()//debounce after delay

//        collectionRef.orderBy("time", Query.Direction.DESCENDING)
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    val id = document.id
//                    val imageURL = document.getString("imageURL") ?: ""
//                    val price = document.getLong("price")?.toInt() ?: 0
//                    val type = document.getString("type") ?: ""
//                    val name = document.getString("name") ?: ""
//                    val time = document.getDate("time")
//
//                    // Log or process the data as needed
//                    Log.d("menu_admin", "Document ID: $id, Image URL: $imageURL, Price: $price, Type: $type, Name: $name, Time: $time")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.e("menu_admin", "Error fetching documents: $exception")
//            }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view_tag, veg_foods())
            .commit()

        binding.vegBtn.setOnClickListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view_tag) as?  veg_foods
            fragment?.updateData(filterVegItems())
        }

        binding.nonvegBtn.setOnClickListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view_tag) as? veg_foods
            fragment?.updateData(filterNonVegItems())
        }

        // Apply padding to the main view to prevent content from being obscured by system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
//    private fun disableButtons() {
//        binding.vegBtn.isEnabled = false
//        binding.nonvegBtn.isEnabled = false
//    }

//    private fun enableButtonsAfterDelay() {
//        Handler(Looper.getMainLooper()).postDelayed({
//            binding.vegBtn.isEnabled = true
//            binding.nonvegBtn.isEnabled = true
//        }, debounceThreshold)
//    }

    private fun showToast(message: String) {
        // Check if there's a current toast, cancel it before showing the new one
        currentToast?.cancel()
        // Show the new toast
        currentToast = Toast.makeText(this@View_Menu_admin, message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }
    private fun fetchDataFromFirestore() {
        val db = Firebase.firestore
        val collectionRef = db.collection("food_details")

        collectionRef
            .orderBy("time", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                foodItemList.clear()
                for (document in documents) {
                    val id = document.id
                    val imageURL = document.getString("image") ?: ""
                    val price = (document.getLong("price") ?: 0).toInt()
                    val name = document.getString("name") ?: ""
                    val time = document.getDate("time").toString()
                    val type = document.getString("type") ?: ""

                    val foodItem = FoodItemData(id, imageURL, price, type, name, time)
                    foodItemList.add(foodItem)
                }
//                updateFragmentWithFilteredData("veg") // Initially show veg items
            }
            .addOnFailureListener { exception ->
                Log.e("view_admin", "Error fetching documents: $exception")
            }
    }
    private fun filterVegItems(): List<FoodItemData> {
        return foodItemList.filter { it.type == "Veg" }
    }

    private fun filterNonVegItems(): List<FoodItemData> {
        return foodItemList.filter { it.type == "Non-veg" }
    }

//    private fun replaceFragment(fragment: Fragment) {
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.fragment_container_view_tag, fragment)
//        fragmentTransaction.commit()
//    }
}
