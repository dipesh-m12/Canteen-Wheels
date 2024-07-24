package com.example.canteenwheels

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MyAdapter_orderreceiver_admin(private val context: Context
    ,private val cartItems: List<CartItem>, private val listener: OnItemClickListener1
) : RecyclerView.Adapter<MyAdapter_orderreceiver_admin.CartViewHolder1>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter_orderreceiver_admin.CartViewHolder1 {
        val view = LayoutInflater.from(context).inflate(R.layout.orderreceived_card, parent, false)
        return CartViewHolder1(view)
    }
    override fun onBindViewHolder(holder: MyAdapter_orderreceiver_admin.CartViewHolder1, position: Int) {
        val currentItem = cartItems[position]
        holder.bind(currentItem, listener)
        holder.itemView.setOnClickListener(){
            listener.onItemClick(currentItem )
        }
    }
    override fun getItemCount(): Int {
        return cartItems.size
    }
    inner class CartViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val id : TextView = itemView.findViewById(R.id.itemview_orderid)
        val item: TextView = itemView.findViewById(R.id.itemview_name)
        val total: TextView = itemView.findViewById(R.id.itemview_total)
        val quantity: TextView = itemView.findViewById(R.id.itemview_quantity)
        val status: TextView = itemView.findViewById(R.id.itemview_status)
        val completed: TextView = itemView.findViewById(R.id.itemview_completed)
        val image:ImageView = itemView.findViewById(R.id.itemview_image)

        fun bind(cartItem: CartItem, listener: OnItemClickListener1) {
            id.text=cartItem.username
            item.text = cartItem.item
            total.text = cartItem.total
            quantity.text = cartItem.quantity
            status.text = cartItem.status
            completed.text = cartItem.completed


            status.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.nonveg_food))
            completed.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.nonveg_food))

            if (cartItem.status == "confirmed") {
                status.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.veg_food))
            }
            if(cartItem.completed=="true"){
                completed.backgroundTintList=
                    ColorStateList.valueOf(ContextCompat.getColor(context,R.color.veg_food))
            }

            Picasso.get()
                .load(cartItem.image)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(image)


        }
    }
}