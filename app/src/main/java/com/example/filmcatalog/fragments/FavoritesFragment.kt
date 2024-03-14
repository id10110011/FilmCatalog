package com.example.filmcatalog.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import com.example.filmcatalog.activities.MovieActivity
import com.example.filmcatalog.adapters.CatalogAdapter
import com.example.filmcatalog.databinding.FragmentFavoritesBinding
import com.example.filmcatalog.models.Movie
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage

class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var catalogAdapter: CatalogAdapter

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var db: FirebaseFirestore
    private lateinit var collectionReference: CollectionReference

    private lateinit var storage: FirebaseStorage
    private lateinit var movies: ArrayList<Movie>
    private lateinit var docNames: ArrayList<String>

    private val collectionName = "favorites"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentFavoritesBinding.inflate(layoutInflater)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        collectionReference = db.collection(collectionName)
        storage = FirebaseStorage.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        movies = ArrayList<Movie>()
        docNames = ArrayList<String>()
        val userEmail = firebaseAuth.currentUser?.email.toString()

        collectionReference.document(userEmail).get(Source.DEFAULT)
            .addOnCompleteListener {
                if (it.isSuccessful && it.result.get("refArray") != null) {
                    val refArray = it.result.get("refArray") as ArrayList<DocumentReference>
                    for (docRef in refArray) {
                        initMovies(docRef)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity, it.localizedMessage + userEmail, Toast.LENGTH_LONG).show()
            }
    }

    private fun initMovies(documentReference: DocumentReference) {
        documentReference.get(Source.DEFAULT)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    docNames.add(it.result.id)
                    val movie = it.result.toObject(Movie::class.java)
                    movies.add(Movie(movie!!))

                    if (isAdded) {
                        catalogAdapter = CatalogAdapter(requireActivity(), movies)
                        catalogAdapter.notifyDataSetChanged()
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
                Toast.makeText(activity, it.localizedMessage + "damndamdanmda", Toast.LENGTH_LONG)
                    .show()
            }
    }
}