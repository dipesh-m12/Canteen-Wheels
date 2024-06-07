package com.example.canteenwheels

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.canteenwheels.databinding.ActivityHeroBinding

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

interface OnItemClickListener {
    fun onItemClick(item: FoodCard)
}
class Hero : AppCompatActivity(),OnItemClickListener {
    private lateinit var binding:ActivityHeroBinding
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var foodCards: MutableList<FoodCard>
    private var currentToast: Toast? = null
    private lateinit var useremail:String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(this@Hero,"Welcome!",Toast.LENGTH_SHORT).show()
        enableEdgeToEdge()
        auth = Firebase.auth
        binding = ActivityHeroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val currentUser = auth.currentUser
        useremail = currentUser?.email ?: ""
        if(currentUser==null){
            val intent = Intent(this@Hero, Login::class.java)
            startActivity(intent)
            finish()
        }else{
            binding.userInfo.text= currentUser.email
        }
        binding.logout.setOnClickListener(){
            Firebase.auth.signOut()
            val intent = Intent(this@Hero, Login::class.java)
            startActivity(intent)
            finish()
        }
        binding.cart.setOnClickListener(){
            val intent = Intent(this@Hero, cart_user::class.java)    //remove later
            intent.putExtra("useremail",useremail)
            startActivity(intent)
        }
        setupRecyclerView()
        binding.vegbtn.setOnClickListener {
            showToast("Veg")
            filterFoodItems("Veg")
        }

        binding.nonvegbtn.setOnClickListener {
            showToast("Non-Veg")
            filterFoodItems("Non-veg")
        }
        binding.searchbarUser.clearFocus()
        binding.searchbarUser.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    (binding.recyclerUser.adapter as MyAdapter_hero_user).filter(it)
                }
                return true
            }
        })
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun filterFoodItems(type: String) {
        val filteredList = foodCards.filter { it.type == type }
        (binding.recyclerUser.adapter as MyAdapter_hero_user).submitList(filteredList)
    }


    private fun setupRecyclerView() {
        binding.progressbarUser.visibility= View.VISIBLE
        db.collection("food_details")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this@Hero, "No items found", Toast.LENGTH_LONG).show()
                }
                foodCards = mutableListOf()
                for (document in documents) {
                    val name = document.getString("name") ?: ""
                    val type = document.getString("type") ?: ""
                    val image = document.getString("image") ?: ""
                    val price = document.getLong("price")?.toInt() ?: 0
                    val foodCard = FoodCard(name, type, image,price)
                    foodCards.add(foodCard)
                    binding.progressbarUser.visibility= View.GONE
                }
                val filteredFoodCards = foodCards.filter { it.type == "Veg" }
                val adapter = MyAdapter_hero_user(this)
                adapter.submitList(filteredFoodCards)
                with(binding.recyclerUser) {
                    layoutManager = LinearLayoutManager(this@Hero)
                    this.adapter = adapter
                    this.itemAnimator = DefaultItemAnimator()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

    }
    private fun showToast(message: String) {
        // Check if there's a current toast, cancel it before showing the new one
        currentToast?.cancel()
        // Show the new toast
        currentToast = Toast.makeText(this@Hero, message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }
    override fun onItemClick(item: FoodCard) {
        showToast(item.name)
        val intent = Intent(this@Hero, confirm_order::class.java)
        intent.putExtra("username", useremail)
        intent.putExtra("itemname", item.name)
        intent.putExtra("type", item.type)
        intent.putExtra("image", item.image)
        intent.putExtra("price", item.price)
        startActivity(intent)
    }

}
