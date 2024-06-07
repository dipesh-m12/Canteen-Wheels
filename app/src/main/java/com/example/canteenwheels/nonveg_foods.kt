package com.example.canteenwheels

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class nonveg_foods : Fragment(), MyAdapterAdmin.OnItemClickListener {

    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapterAdmin
    private val foodItemList = mutableListOf<FoodItemData>()
    private lateinit var progressBar: ProgressBar

    private lateinit var searchView: SearchView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d("veg_foods", "f2onCreateView called")
        val rootView = inflater.inflate(R.layout.fragment_nonveg_foods, container, false)

        progressBar = rootView.findViewById<ProgressBar>(R.id.progressbar_nonveg_foods)
        searchView = rootView.findViewById(R.id.nonveg_search_bar)
        recyclerView = rootView.findViewById(R.id.recyclerView_admin2) // Update to correct id
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        adapter = MyAdapterAdmin(foodItemList)
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = DefaultItemAnimator()
        adapter.setOnItemClickListener(this)

        fetchDataFromFirestore()
        searchView.clearFocus()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("nonveg_foods", "onQueryTextSubmit: $query")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("nonveg_foods", "onQueryTextChange: $newText")
                adapter.filter(newText.orEmpty())
                updateItemCount(newText.orEmpty())
                return true
            }
        })

        return rootView
    }

    private fun updateItemCount(query: String) {
        val filteredItemCount = if (query.isEmpty()) {
            foodItemList.size
        } else {
            adapter.itemCount
        }
        val nonvegItemCountTextView = requireView().findViewById<TextView>(R.id.nonveg_item_count)
        nonvegItemCountTextView.text = filteredItemCount.toString()
    }

    private fun fetchDataFromFirestore() {
        progressBar.visibility = View.VISIBLE
        Log.d("veg_foods", "fetchDataFromFirestore called")
        val db = Firebase.firestore
        val collectionRef = db.collection("food_details")
        Log.d("veg_foods_only","outside")

        collectionRef
            .orderBy("time", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {

                    if (documents.isEmpty) {
                        Toast.makeText(requireContext(), "No non veg found", Toast.LENGTH_LONG).show()
                    }
                    val type = document.getString("type")
                    if (type == "Non-veg") {
                        val id = document.id
                        val imageURL = document.getString("image") ?: ""
                        val price = (document.getLong("price") ?: 0).toInt()
                        val name = document.getString("name") ?: ""
                        val time = document.getDate("time").toString()

                        val foodItem = FoodItemData(id, imageURL, price, type, name, time)
                        foodItemList.add(foodItem)
                        Log.d("veg_foods_only", "Added item to foodItemList: $foodItem")
                    }
                    val nonvegItemCount = foodItemList.size
                    val nonvegItemCountTextView = requireView().findViewById<TextView>(R.id.nonveg_item_count)
                    nonvegItemCountTextView.text = nonvegItemCount.toString()
                }
                adapter.notifyDataSetChanged() // Notify adapter of data change
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                Log.e("menu_admin", "Error fetching documents: $exception")
            }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            nonveg_foods().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onItemClick(position: Int) {
        Log.d("nonveg_foods","in listener")
        val item = foodItemList[position]
        val intent = Intent(requireContext(), DeleteItem_admin::class.java)
        intent.putExtra("itemId", item.id)
        intent.putExtra("itemName", item.name)
        intent.putExtra("itemImage", item.imageURL)
        intent.putExtra("itemPrice", item.price)
        intent.putExtra("itemTime", item.time)
        intent.putExtra("itemType", item.type)
        startActivity(intent)
    }
}