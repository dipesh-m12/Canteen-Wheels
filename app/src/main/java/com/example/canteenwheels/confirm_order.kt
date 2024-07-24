package com.example.canteenwheels

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.canteenwheels.databinding.ActivityConfirmOrderBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat


class confirm_order : AppCompatActivity() {
    private lateinit var binding: ActivityConfirmOrderBinding
    private val db = Firebase.firestore
    private var currentToast: Toast? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityConfirmOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        createNotificationChannel()

        val username = intent.getStringExtra("username")
        val itemName = intent.getStringExtra("itemname")
        val type = intent.getStringExtra("type")
        val image = intent.getStringExtra("image")
        val price = intent.getIntExtra("price", 0)
        var quantity = 1

        Log.d("confirmorder","user $username")
        binding.quantity.text=quantity.toString()
        binding.name.text=itemName
        binding.type.text=type
        binding.price.text=price.toString()
        Picasso.get()
            .load(image)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(binding.image)

        binding.quantity.text=quantity.toString()
        binding.total.text=price.toString()

        binding.plus.setOnClickListener(){
            if(quantity>0 && quantity<=9) {
                quantity++
                binding.quantity.text=quantity.toString()
                binding.total.text=(price*quantity).toString()
            }else{
                showToast("Maximum Limit Reached!")
            }

        }
        binding.minus.setOnClickListener(){
            if(quantity>1 && quantity<=10) {
                quantity--
                binding.quantity.text=quantity.toString()
                binding.total.text=(price*quantity).toString()
            }else{
                showToast("Minimum Limit Reached!")
            }

        }
        binding.ConfirmBtn.setOnClickListener(){
            binding.progressBarConfirmorder.visibility=View.VISIBLE
            val time_stamp= FieldValue.serverTimestamp()
            val currentDateTime = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formattedDateTime = formatter.format(currentDateTime)

            val orderDetails = hashMapOf(
                "username" to username,
                "item" to itemName,
                "price" to price,
                "quantity" to quantity,
                "time" to time_stamp,
                "total" to price * quantity,
                "type" to type,
                "image" to image,
                "status" to "pending",
                "completed" to false
            )

            db.collection("order_details")
                .add(orderDetails)
                .addOnSuccessListener {
                    sendNotificationToUser("Your order has been placed successfully!")
                    showToast("Order placed successfully! See Cart")
                    binding.id.text=it.id
                    binding.time.text=formattedDateTime
                    binding.ConfirmBtn.text="Requested!"
                    binding.ConfirmBtn.isEnabled=false
                    binding.minus.isEnabled=false
                    binding.plus.isEnabled=false
                    binding.progressBarConfirmorder.visibility=View.GONE
                }
                .addOnFailureListener { e ->
                    showToast("Failed to place order: ${e.message}")
                }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun sendNotificationToUser(message: String) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.g12)
            .setContentTitle("Order Notification")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@confirm_order,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(0, builder.build())
        }
    }

    // Function to create the notification channel
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the systemval notificationManager: NotificationManager =
            //    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showToast(message: String) {
        // Check if there's a current toast, cancel it before showing the new one
        currentToast?.cancel()
        // Show the new toast
        currentToast = Toast.makeText(this@confirm_order, message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }
    companion object {
        private const val CHANNEL_ID = "order_channel"
    }
}