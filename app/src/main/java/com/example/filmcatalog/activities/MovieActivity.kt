package com.example.filmcatalog.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.filmcatalog.R
import com.example.filmcatalog.adapters.ImageAdapter
import com.example.filmcatalog.databinding.ActivityMovieBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

class MovieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieBinding
    private lateinit var pageChangeListener: ViewPager2.OnPageChangeCallback

    private val collectionFavs = "favorites"
    private val collectionMovies = "movies"
    private lateinit var db: FirebaseFirestore
    private lateinit var collectionRefFavs: CollectionReference
    private lateinit var movieDocumentReference: DocumentReference

    private lateinit var firebaseAuth: FirebaseAuth

    private val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(8, 0, 8, 0)
        }

    private lateinit var docName: String
    private lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        userEmail = firebaseAuth.currentUser?.email.toString()

        db = FirebaseFirestore.getInstance()
        collectionRefFavs = db.collection(collectionFavs)

        initPage()
        setImageToButtonFavorite()
        setListeners()
    }

    private fun setImageToButtonFavorite() {
        collectionRefFavs.document(userEmail).get(Source.DEFAULT)
            .addOnCompleteListener {
                if (it.isSuccessful && it.result.get("refArray") != null) {
                    val refArray = ArrayList<DocumentReference>()
                    refArray.addAll(it.result.get("refArray") as Collection<DocumentReference>)
                    if (refArray.contains(movieDocumentReference)) {
                        binding.addToFavoritesButton.setImageResource(R.drawable.ic_bookmark_added)
                    } else {
                        binding.addToFavoritesButton.setImageResource(R.drawable.ic_bookmark)
                    }
                } else {
                    binding.addToFavoritesButton.setImageResource(R.drawable.ic_bookmark)
                }
            }
            .addOnFailureListener {
                binding.addToFavoritesButton.setImageResource(R.drawable.ic_bookmark)
            }
    }

    private fun setListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.addToFavoritesButton.setOnClickListener {
            collectionRefFavs.document(userEmail).get(Source.DEFAULT)
                .addOnCompleteListener {
                    var refArray = ArrayList<DocumentReference>()
                    if (it.isSuccessful && it.result.get("refArray") != null) {
                        refArray = it.result.get("refArray") as ArrayList<DocumentReference>
                        if (refArray.contains(movieDocumentReference)) {
                            refArray.remove(movieDocumentReference)
                        } else {
                            refArray.add(movieDocumentReference)
                        }
                    } else {
                        refArray.add(movieDocumentReference)
                    }
                    setReferenceArray(refArray)
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun setReferenceArray(refArray: ArrayList<DocumentReference>) {
        collectionRefFavs.document(userEmail).set(
            mapOf(
                "refArray" to refArray
            ))
            .addOnCompleteListener {
                finish()
                startActivity(intent)
            }
            .addOnFailureListener { ex ->
                Toast.makeText(this, ex.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun initPage() {
        val intent = this.intent
        if (intent != null) {
            docName = intent.getStringExtra("docName").toString()
            movieDocumentReference = db.collection(collectionMovies).document(docName)

            val name = intent.getStringExtra("name")
            val description = intent.getStringExtra("description")
            val pictures = ArrayList<String>()
            intent.getStringArrayListExtra("pictureNames")?.let { pictures.addAll(it) }
            val pictureNames = pictures.subList(1, pictures.size)

            fillMovieFields(name!!, description!!, pictures[0])
            initAdapter(pictureNames)
            initSlider(pictureNames.size)

            binding.viewPager.registerOnPageChangeCallback(pageChangeListener)
        }
    }

    private fun fillMovieFields(name: String, description: String, iconUrl: String) {
        binding.movieName.text = name
        binding.movieDescription.text = description
        Glide.with(this)
            .load(Uri.parse(iconUrl))
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(binding.movieIcon)
    }

    private fun initAdapter(pictureNames: MutableList<String>) {
        val imageAdapter = ImageAdapter()
        binding.viewPager.adapter = imageAdapter
        imageAdapter.submitList(pictureNames)
    }

    private fun initSlider(size: Int) {
        val dotsImage = Array(size) {
            ImageView(this)
        }

        dotsImage.forEach {
            it.setImageResource(
                R.drawable.non_active_dot
            )
            binding.viewPagerDots.addView(it, params)
        }

        dotsImage[0].setImageResource(R.drawable.active_dot)

        initPageChangeListener(dotsImage)
    }

    private fun initPageChangeListener(dotsImage: Array<ImageView>) {
        pageChangeListener = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                dotsImage.mapIndexed { index, imageView ->
                    if (position == index) {
                        imageView.setImageResource(
                            R.drawable.active_dot
                        )
                    } else {
                        imageView.setImageResource(
                            R.drawable.non_active_dot
                        )
                    }
                }
                super.onPageSelected(position)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeListener)
    }
}