package com.example.canteenwheels

import com.google.firebase.Timestamp
import java.util.Date

data class FoodItemData(
    val id: String,
    val imageURL: String,
    val price: Int,
    val type: String,
    val name: String,
    val time: String
)
