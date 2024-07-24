package com.example.canteenwheels

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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
        val card:RelativeLayout=itemView.findViewById(R.id.card)
        val text:TextView=itemView.findViewById(R.id.tag)
        fun bind(cartItem: CartItem, listener: OnItemClickListener1) {
            id.text=cartItem.id
            item.text = cartItem.item
            total.text = cartItem.total
            type.text = cartItem.type
            status.text = if(cartItem.status=="confirmed") "Confirmed" else "Pending"
            completed.text = if(cartItem.completed=="true" ) "Ready" else if(cartItem.completed=="false" && cartItem.status=="confirmed") "Cooking" else "..."


            status.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.nonveg_food))
            completed.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.nonveg_food))

            if (cartItem.status == "confirmed") {
                Log.d("Cart_user", cartItem.completed)
                status.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.veg_food))
            }
            if(cartItem.completed=="true"){
                completed.backgroundTintList=ColorStateList.valueOf(ContextCompat.getColor(context,R.color.veg_food))
            }
//            if(cartItem.status == "pending"){
//                card.alpha=0.5f
//            }
            Picasso.get()
                .load(cartItem.image)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(image)

            text.setCompoundDrawableTintList(ContextCompat.getColorStateList(itemView.context, R.color.veg_food))
            text.setBackgroundResource(R.drawable.veg_btn_bg)

            if(cartItem.type=="Non-veg"){
                text.setCompoundDrawableTintList(ContextCompat.getColorStateList(itemView.context, R.color.nonveg_food))
                text.setBackgroundResource(R.drawable.nonveg_btn_bg)
            }


        }
    }

}
