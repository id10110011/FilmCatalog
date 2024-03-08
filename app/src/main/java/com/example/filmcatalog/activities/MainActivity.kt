package com.example.filmcatalog.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.filmcatalog.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.catalogBox.setOnClickListener {
            startActivity(Intent(this, CatalogActivity::class.java))
        }

        binding.favoritesBox.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        binding.profileBox.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}