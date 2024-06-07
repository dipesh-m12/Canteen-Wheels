package com.example.canteenwheels

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.util.Locale

data class FoodCard(
    val name: String,
    val type: String,
    val image: String,
    val price: Int
)



class MyAdapter_hero_user(private val listener: OnItemClickListener) : RecyclerView.Adapter<MyAdapter_hero_user.ViewHolder>() {

    private var itemList: MutableList<FoodCard> = mutableListOf()
    private var filteredList: MutableList<FoodCard> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_fooditem_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = filteredList[position]
        holder.bind(currentItem)
        holder.itemView.setOnClickListener {
            listener.onItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    fun submitList(list: List<FoodCard>) {
        itemList.clear()
        itemList.addAll(list)
        filter("")
    }
    fun filter(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(itemList)
        } else {
            val lowerCaseQuery = query.lowercase(Locale.getDefault())
            for (item in itemList) {
                if (item.name.lowercase(Locale.getDefault()).contains(lowerCaseQuery)) {
                    filteredList.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.itemview_name)
        private val typeTextView: TextView = itemView.findViewById(R.id.itemview_type)
        private val priceTextView: TextView = itemView.findViewById(R.id.itemview_price)
        private val imageView: ImageView = itemView.findViewById(R.id.itemview_image)

        fun bind(item: FoodCard) {

            if (item.image.isNotEmpty()) {
                Picasso.get().load(item.image)
                    .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                    .error(R.drawable.error_image)             // Error image if loading fails
                    .into(imageView, object : Callback {
                        override fun onSuccess() {
                            // Image loaded successfully
                        }

                        override fun onError(e: Exception?) {
                            Log.e("Picasso", "Failed to load image: ${item.image}", e)
                        }
                    })
            } else {
                Log.e("Picasso", "Empty imageURL for item: ${item.image}")
            }
            nameTextView.text=item.name
            typeTextView.text=item.type
            priceTextView.text=item.price.toString()
        }
    }
}
