package com.example.canteenwheels

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


data class CartItem(
    val id:String,
    val username: String,
    val item: String,
    val price: String,
    val quantity: String,
    val time: String,
    val total: String,
    val type: String,
    val image: String,
    val status: String,
    val completed: String
)



class MyAdapter_cart_user(
    private val context: Context,
    private val cartItems: List<CartItem>,
    private val listener: OnItemClickListener1
) : RecyclerView.Adapter<MyAdapter_cart_user.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cart_card_user, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = cartItems[position]
        holder.bind(currentItem, listener)
        holder.itemView.setOnClickListener(){
            listener.onItemClick(currentItem )
        }

    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val id : TextView = itemView.findViewById(R.id.itemview_orderid)
        val item: TextView = itemView.findViewById(R.id.itemview_name)
        val total: TextView = itemView.findViewById(R.id.itemview_total)
        val type: TextView = itemView.findViewById(R.id.itemview_type)
        val status: TextView = itemView.findViewById(R.id.itemview_status)
        val completed: TextView = itemView.findViewById(R.id.itemview_completed)
        val image:ImageView = itemView.findViewById(R.id.itemview_image)

        fun bind(cartItem: CartItem, listener: OnItemClickListener1) {
            id.text=cartItem.id
            item.text = cartItem.item
            total.text = cartItem.total
            type.text = cartItem.type
            status.text = cartItem.status
            completed.text = cartItem.completed

            Picasso.get()
                .load(cartItem.image)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(image)


        }
    }

}
