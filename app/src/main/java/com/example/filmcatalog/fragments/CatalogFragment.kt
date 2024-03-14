package com.example.filmcatalog.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.filmcatalog.R
import com.example.filmcatalog.activities.MovieActivity
import com.example.filmcatalog.adapters.CatalogAdapter
import com.example.filmcatalog.databinding.FragmentCatalogBinding
import com.example.filmcatalog.models.Movie
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage

class CatalogFragment : Fragment() {
    private val collectionName = "movies"

    private lateinit var binding: FragmentCatalogBinding
    private lateinit var catalogAdapter: CatalogAdapter

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var collectionReference: CollectionReference

    private lateinit var storage: FirebaseStorage
    private lateinit var movies: ArrayList<Movie>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCatalogBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        collectionReference = db.collection(collectionName)
        storage = FirebaseStorage.getInstance()

        initGridItems()
        setListeners()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
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

                    if (isAdded) {
                        catalogAdapter = CatalogAdapter(
                            requireActivity(),
                            movies.clone() as ArrayList<Movie>
                        )
                        binding.gridView.adapter = catalogAdapter
                        binding.gridView.isClickable = true
                    }

                    binding.gridView.onItemClickListener =
                        AdapterView.OnItemClickListener { adapterView, view, i, l ->
                            val intent = Intent(activity, MovieActivity::class.java)
                            intent.putExtra("name", movies[i].name)
                            intent.putExtra("description", movies[i].description)
                            intent.putStringArrayListExtra("pictureNames", movies[i].pictureNames)
                            intent.putExtra("docName", docNames[i])

                            startActivity(intent)
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_LONG).show()
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