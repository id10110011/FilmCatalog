package com.example.filmcatalog.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.example.filmcatalog.R
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
                    catalogAdapter = CatalogAdapter(this@CatalogActivity, movies)
                    binding.gridView.adapter = catalogAdapter
                    binding.gridView.isClickable = true

                    binding.gridView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
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

        binding.menuButton.setOnClickListener {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val menuItem = menu?.findItem(R.id.search_view)
        val searchView = menuItem?.actionView as SearchView

        searchView.maxWidth = Int.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.e("TAG", " new text ==> " + newText)
                catalogAdapter.filter.filter(newText)

                return true
            }

        })
        return true
    }
}
