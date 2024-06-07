package com.example.canteenwheels

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.canteenwheels.databinding.ActivityAdminConfigBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

class Admin_Config : AppCompatActivity() {
    private lateinit var binding: ActivityAdminConfigBinding
    private val storageRef = Firebase.storage.reference
    private val db = Firebase.firestore


    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Image picked successfully, get the URI
                val imageUri = result.data?.data
                imageUri?.let { uri ->
                    // Upload the image to Firebase Storage
                    binding.progressBarAdminConfig.visibility = View.VISIBLE
                    uploadImage(uri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val options = arrayOf("Select type", "Veg", "Non-veg") // Include "Select type" as the first option
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.saveButton.setOnClickListener {
            binding.progressBarAdminConfig.visibility = View.GONE
            val foodName = binding.foodNameEditText.text.toString()
            val foodPrice = binding.foodPriceEditText.text.toString()
            val foodType = binding.foodTypeSpinner.selectedItem.toString()

            if (foodName.isEmpty() || foodPrice.isEmpty() || foodType == "Select type") {
                // Show a toast message indicating that all fields are required
                Toast.makeText(this@Admin_Config, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Check if an image has been selected
            val imageUrl = binding.selectedImageTextView.text.toString()
            if (imageUrl=="No image selected" || imageUrl=="") {
                Toast.makeText(this@Admin_Config, "Please select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.progressBarAdminConfig.visibility = View.VISIBLE
            // Create a Firestore document with the food details
            val food = hashMapOf(
                "name" to foodName,
                "price" to foodPrice.toInt(),
                "image" to imageUrl,
                "type" to foodType,
                "time" to FieldValue.serverTimestamp()
            )

            // Add the document to the Firestore collection
            db.collection("food_details")
                .add(food)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    Toast.makeText(this@Admin_Config,"Added to Menu!",Toast.LENGTH_SHORT).show()
                    binding.foodNameEditText.text.clear()
                    binding.foodPriceEditText.text.clear()
                    binding.selectedImageTextView.text = ""
                    binding.foodTypeSpinner.setSelection(0)
                    binding.progressBarAdminConfig.visibility = View.GONE
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    Toast.makeText(this@Admin_Config,"Failed attempt!",Toast.LENGTH_SHORT).show()
                }

        }

        binding.selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*" // Filter to show only images
            }
            pickImageLauncher.launch(intent)
        }

        binding.foodTypeSpinner.adapter = adapter

        binding.ViewMenu.setOnClickListener(){
            val intent =Intent(this@Admin_Config,View_Menu_admin::class.java)
            startActivity(intent)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun uploadImage(imageUri: Uri) {
        val imageName = UUID.randomUUID().toString()
        val imageRef = storageRef.child("images/$imageName")

        val uploadTask = imageRef.putFile(imageUri)
        uploadTask.continueWithTask { task ->
            binding.progressBarAdminConfig.visibility = View.GONE
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                binding.selectedImageTextView.text = downloadUri.toString()
            } else {
                // Handle failures
                Toast.makeText(this@Admin_Config, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }
    }


    companion object {
        private const val TAG = "Admin_Config"
    }
}
