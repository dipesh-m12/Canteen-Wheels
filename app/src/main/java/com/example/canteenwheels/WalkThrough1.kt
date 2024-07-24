package com.example.canteenwheels

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.canteenwheels.databinding.ActivityMainBinding
import com.example.canteenwheels.databinding.ActivityWalkThrough1Binding

class WalkThrough1 : AppCompatActivity() {
    private lateinit var binding : ActivityWalkThrough1Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalkThrough1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        binding.next.setOnClickListener(){
            val intent = Intent(this@WalkThrough1,Register::class.java)
            startActivity(intent)
            finish()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}