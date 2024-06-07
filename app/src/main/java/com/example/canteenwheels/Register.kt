package com.example.canteenwheels

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.canteenwheels.databinding.ActivityRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.submitBtnRegister.setOnClickListener(){
            binding.progressBarRegister.visibility = View.VISIBLE
            val email = binding.emailRegister.text.toString()
            val password = binding.passRegister.text.toString()
            val confirmPassword = binding.confirmPassRegister.text.toString()

            if (email.length <= 8) {
                Toast.makeText(this@Register, "Email must be longer than 8 characters", Toast.LENGTH_SHORT).show()
                binding.progressBarRegister.visibility=View.GONE
                return@setOnClickListener
            }


            if (password.length <= 5) {
                Toast.makeText(this@Register, "Password must be longer than 5 characters", Toast.LENGTH_SHORT).show()
                binding.progressBarRegister.visibility=View.GONE
                return@setOnClickListener
            }


            if (password != confirmPassword) {
                Toast.makeText(this@Register, "Password and confirm password do not match", Toast.LENGTH_SHORT).show()
                binding.progressBarRegister.visibility=View.GONE
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.progressBarRegister.visibility = View.GONE
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        Toast.makeText(baseContext, "Logged IN!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@Register, Hero::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e("RegisterActivity", "Authentication failed: ${task.exception?.message}")
                        Toast.makeText(
                            baseContext,
                            "Authentication failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    binding.progressBarRegister.visibility=View.GONE
                }



        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set onClick listener for the submit button
        binding.navToLogin.setOnClickListener {
            // Create an Intent to start the Login activity
            val intent = Intent(this@Register, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}
