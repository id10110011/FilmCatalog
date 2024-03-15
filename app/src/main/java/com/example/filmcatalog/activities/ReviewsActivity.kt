package com.example.filmcatalog.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.filmcatalog.adapters.ReviewsAdapter
import com.example.filmcatalog.databinding.ActivityReviewsBinding
import com.example.filmcatalog.models.Review
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

class ReviewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewsBinding
    private lateinit var reviewsAdapter: ReviewsAdapter

    private lateinit var db: FirebaseFirestore
    private lateinit var collectionReference: CollectionReference

    private lateinit var reviewsMap: List<Map<String, Any>>
    private lateinit var reviews: ArrayList<Review>

    private val collectionMoviesName = "movies"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        collectionReference = db.collection(collectionMoviesName)

        initPage()
        setListeners()
    }

    private fun initPage() {
        val docName = intent.getStringExtra("movieDocName")
        if (docName != null)
            db.collection(collectionMoviesName).document(docName).get(Source.DEFAULT)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (it.result.get("reviews") != null) {
                            reviewsMap = it.result.get("reviews") as List<Map<String, Any>>
                            reviews = convertMapToArrayList(reviewsMap)

                            reviewsAdapter = ReviewsAdapter(this, reviews)
                            binding.gridReviews.adapter = reviewsAdapter
                        }
                    }

                }
    }

    private fun setListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun convertMapToArrayList(reviewsMap: List<Map<String, Any>>): ArrayList<Review> {
        val reviews = ArrayList<Review>()
        for (reviewMap in reviewsMap) {
            reviews.add(
                Review(
                    reviewMap["author"].toString(), reviewMap["authorEmail"].toString(), reviewMap["text"].toString(),
                    reviewMap["createdDate"].toString(), (reviewMap["rating"] as Number).toFloat()
                )
            )
        }
        return reviews
    }
}