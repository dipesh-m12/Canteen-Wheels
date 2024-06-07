package com.example.canteenwheels

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.canteenwheels.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.submitBtnLogin.setOnClickListener(){
            binding.progressBarLogin.visibility = View.VISIBLE
            val email = binding.emailInputEditText.text.toString()
            val password = binding.passRegister.text.toString()

            if (email.length <= 8) {
                Toast.makeText(this@Login, "Email must be longer than 8 characters", Toast.LENGTH_SHORT).show()
                binding.progressBarLogin.visibility=View.GONE
                return@setOnClickListener
            }


            if (password.length <= 5) {
                Toast.makeText(this@Login, "Password must be longer than 5 characters", Toast.LENGTH_SHORT).show()
                binding.progressBarLogin.visibility=View.GONE
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.progressBarLogin.visibility=View.GONE
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        Toast.makeText(
                            baseContext,
                            "Login Successful!.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        if(password=="canteen_admin"){
                            Log.d("Login","Here")
                            val intent = Intent(this@Login, OrderReceiver_admin::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            val intent = Intent(this@Login, Hero::class.java)
                            startActivity(intent)
                            finish()
                        }

                    } else {
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set onClick listener for the navigation button to Register activity
        binding.navToRegister.setOnClickListener {
            // Create an Intent to start the Register activity
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)
            finish()
        }
    }
}
