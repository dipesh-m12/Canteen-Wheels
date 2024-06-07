package com.example.canteenwheels

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import android.widget.Filterable


class MyAdapterAdmin(private val itemList: List<FoodItemData>) : RecyclerView.Adapter<ViewHolder_admin>() , Filterable{

    private lateinit var mListener: OnItemClickListener
    private var filteredList: List<FoodItemData> = itemList

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_admin {
//        Log.d("MyAdapter", "onCreateViewHolder called")
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fooditem_view, parent, false)
        return ViewHolder_admin(itemView)
    }


    override fun onBindViewHolder(holder: ViewHolder_admin, position: Int) {
//        Log.d("MyAdapter", "onBindViewHolder called for position: $position")
        val currentItem = filteredList[position]
        holder.bind(currentItem)
//        Log.d("MyAdapter", "Item at position $position")
        holder.itemView.setOnClickListener {
            mListener.onItemClick(position)
        }
    }


    override fun getItemCount(): Int {
        Log.d("MyAdapter", "getItemCount called, returning ${filteredList.size}")
        return filteredList.size
    }

    fun filter(query: String) {
        val filteredItems = itemList.filter { it.name.lowercase().contains(query.lowercase()) }
        Log.d("MyAdapter", "fil items: $filteredItems")
        filteredList = filteredItems
        notifyDataSetChanged()
        Log.d("MyAdapter", "Filtering with query: $query, Result size: ${filteredList.size}")
    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = mutableListOf<FoodItemData>()
                if (constraint.isNullOrEmpty()) {
                    filteredResults.addAll(itemList)
                } else {
                    val filterPattern = constraint.toString().lowercase().trim()
                    Log.d("MyAdapter","constraint:$constraint")
                    for (item in itemList) {
                        Log.d("MyAdapter","item:$item")
                        if (item.name.lowercase().contains(filterPattern)) {
                            filteredResults.add(item)
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredResults
                Log.d("MyAdapter", "Performing filtering with constraint: $constraint, Result size: ${filteredResults.size}")
                return filterResults
            }
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as List<FoodItemData> ?: mutableListOf()
                notifyDataSetChanged()
                Log.d("MyAdapter", "Publishing results with constraint: $constraint, Result size: ${filteredList.size}")
            }
        }
    }
}
