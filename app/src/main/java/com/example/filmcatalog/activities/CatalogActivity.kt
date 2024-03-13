package com.example.filmcatalog.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.AdapterView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.filmcatalog.adapters.CatalogAdapter
import com.example.filmcatalog.databinding.ActivityCatalogBinding
import com.example.filmcatalog.models.Movie
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage


class CatalogActivity : AppCompatActivity() {
    private val collectionName = "movies"

    private lateinit var binding: ActivityCatalogBinding
    private lateinit var catalogAdapter: CatalogAdapter

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var db: FirebaseFirestore
    private lateinit var collectionReference: CollectionReference

    private lateinit var storage: FirebaseStorage
    private lateinit var movies: ArrayList<Movie>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        collectionReference = db.collection(collectionName)
        storage = FirebaseStorage.getInstance()

        if (firebaseAuth.currentUser == null) {
            firebaseAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        initGridItems()
        setListeners()
    }

    private fun initGridItems() {
        movies = ArrayList()
        val docNames = ArrayList<String>()
        collectionReference.get(Source.DEFAULT)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (doc in it.result) {
                        docNames.add(doc.id)
                        val movie = doc.toObject(Movie::class.java)
                        movies.add(Movie(movie))

                    }

                    catalogAdapter = CatalogAdapter(
                        this@CatalogActivity,
                        movies.clone() as ArrayList<Movie>
                    )
                    binding.gridView.adapter = catalogAdapter
                    binding.gridView.isClickable = true

                    binding.gridView.onItemClickListener =
                        AdapterView.OnItemClickListener { adapterView, view, i, l ->
                            val intent = Intent(this, MovieActivity::class.java)
                            intent.putExtra("name", movies[i].name)
                            intent.putExtra("description", movies[i].description)
                            intent.putStringArrayListExtra("pictureNames", movies[i].pictureNames)
                            intent.putExtra("docName", docNames[i])

                            startActivity(intent)
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun filterList(query: String?) {
        val clearQuery = query?.lowercase()?.trim()
        var filteredList = ArrayList<Movie>()
        filteredList.addAll(movies)
        catalogAdapter.clear()
        if (!clearQuery.isNullOrEmpty()) {
            filteredList =
                filteredList.filter { it.name.lowercase().contains(clearQuery) } as ArrayList<Movie>
        }
        catalogAdapter.addAll(filteredList)
        catalogAdapter.notifyDataSetChanged()
    }

    private fun setListeners() {
        binding.navMenu.menuCatalog.setCardBackgroundColor(Color.parseColor("#3F484A"))
        binding.navMenu.menuFavorites.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
            finish()
        }
        binding.navMenu.menuProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })
    }
}
