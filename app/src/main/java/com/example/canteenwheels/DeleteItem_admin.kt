package com.example.canteenwheels

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.canteenwheels.databinding.ActivityDeleteItemAdminBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso


class DeleteItem_admin : AppCompatActivity() {

    private lateinit var binding : ActivityDeleteItemAdminBinding
    private lateinit var itemId: String
    private lateinit var itemImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDeleteItemAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        itemId = intent.getStringExtra("itemId") ?: ""
        binding.id.text = itemId
        binding.name.text = intent.getStringExtra("itemName") ?: ""
        itemImage = intent.getStringExtra("itemImage") ?: ""
        binding.price.text = intent.getIntExtra("itemPrice", 0).toString()
        binding.time.text = intent.getStringExtra("itemTime") ?: ""
        binding.type.text = intent.getStringExtra("itemType") ?: ""

        Picasso.get()
            .load(itemImage)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(binding.image)

        binding.CancelBtn.setOnClickListener(){
            val intent = Intent(this@DeleteItem_admin,View_Menu_admin::class.java)
            startActivity(intent)
        }
        binding.DeleteBtn.setOnClickListener(){
            binding.progressBardelete.visibility= View.VISIBLE
            deleteItem()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun deleteItem() {
        // Delete item from Firestore
        val db = Firebase.firestore
        val collectionRef = db.collection("food_details")
        collectionRef.document(itemId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Item deleted from Firestore")
                // Delete image from Storage
                itemImage=extractFolderName(itemImage)
                val storageRef = Firebase.storage.reference
                val imageRef = storageRef.child("images/$itemImage")
                imageRef.delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Item deleted!", Toast.LENGTH_SHORT).show()
                        Handler(mainLooper).postDelayed({
                            binding.progressBardelete.visibility= View.GONE
                            val intent = Intent(this@DeleteItem_admin,View_Menu_admin::class.java)
                            startActivity(intent)
                        }, 400)
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error deleting image: $e")
                        binding.progressBardelete.visibility= View.GONE
                        Toast.makeText(this, "Error deleting image", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error deleting item: $e")
                binding.progressBardelete.visibility= View.GONE
                Toast.makeText(this, "Error deleting item", Toast.LENGTH_SHORT).show()
            }

    }
    fun extractFolderName(url: String): String {
        val parts = url.split("/")
        val secondToLast = parts[parts.size-1]
        val subParts = secondToLast.split("?")
        val firstPart = subParts[0]
        val folderName = firstPart.replace("%2F", "/")
        val folderNameParts = folderName.split("/")
        val folderNameExtracted = folderNameParts[folderNameParts.size - 1]
        return folderNameExtracted
    }
    companion object {
        private const val TAG = "DeleteItem_admin"
    }

}