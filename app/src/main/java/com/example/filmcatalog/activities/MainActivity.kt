package com.example.filmcatalog.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.filmcatalog.R
import com.example.filmcatalog.databinding.ActivityMainBinding
import com.example.filmcatalog.fragments.CatalogFragment
import com.example.filmcatalog.fragments.FavoritesFragment
import com.example.filmcatalog.fragments.ProfileFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser == null) {
            firebaseAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            replaceFragment(CatalogFragment())
        }

        binding.bottomMenu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_catalog -> {
                    replaceFragment(CatalogFragment())
                    true
                }
                R.id.menu_favorites -> {
                    replaceFragment(FavoritesFragment())
                    true
                }
                R.id.menu_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        binding.progressBar.visibility = View.GONE
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }


}