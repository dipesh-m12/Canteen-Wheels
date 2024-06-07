package com.example.canteenwheels

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.canteenwheels.FoodItemData
import com.example.canteenwheels.R
import com.example.canteenwheels.Register
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class ViewHolder_admin(itemView: View ) : RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.itemview_image)
    private val nameTextView: TextView = itemView.findViewById(R.id.itemview_name)
    private val typeTextView: TextView = itemView.findViewById(R.id.itemview_type)
    private val priceTextView: TextView = itemView.findViewById(R.id.itemview_price)
    private val timeTextView: TextView = itemView.findViewById(R.id.itemview_time)
    private val idTextView: TextView = itemView.findViewById(R.id.itemview_id)

    private var mListener: MyAdapterAdmin.OnItemClickListener? = null
    init {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                mListener?.onItemClick(position)
            }
        }
    }

    fun bind(item: FoodItemData) {
        if (item.imageURL.isNotEmpty()) {
            Picasso.get().load(item.imageURL)
                .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                .error(R.drawable.error_image)             // Error image if loading fails
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        // Image loaded successfully
                    }

                    override fun onError(e: Exception?) {
                        Log.e("Picasso", "Failed to load image: ${item.imageURL}", e)
                    }
                })
        } else {
            Log.e("Picasso", "Empty imageURL for item: ${item.id}")
        }

        // Bind other data to views
        nameTextView.text = item.name
        typeTextView.text = item.type
        priceTextView.text = item.price?.toString()
        timeTextView.text = item.time?.toString()
        idTextView.text = item.id



    }

}
