package com.example.filmcatalog.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.filmcatalog.databinding.ActivityRateMovieBinding
import com.example.filmcatalog.models.Movie
import com.example.filmcatalog.models.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RateMovieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRateMovieBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var collectionReferenceReviews: CollectionReference


    private lateinit var userEmail: String
    private lateinit var movieDocName: String

    private val collectionReviewsName = "reviews"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRateMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser == null) {
            firebaseAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            userEmail = firebaseAuth.currentUser!!.email.toString()
            movieDocName = intent.getStringExtra("movieDoc").toString()
        }

        db = FirebaseFirestore.getInstance()
        collectionReferenceReviews = db.collection(collectionReviewsName)

        initFields()
        setListeners()
    }

    private fun initFields() {
        binding.ratingBar.rating = intent.getFloatExtra("rating", 1f)
        binding.reviewText.setText(intent.getStringExtra("text"))
    }

    private fun setListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.reviewSaveButton.setOnClickListener {
            val date = Calendar.getInstance().time
            val review = Review(
                userEmail,
                userEmail,
                binding.reviewText.text.toString().replace(Regex("(?m)^[ \t]*\r?\n"), ""),
                SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(date),
                binding.ratingBar.rating
            )
            db.collection("movies").document(movieDocName).get(Source.DEFAULT)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val movie = Movie(it.result.toObject(Movie::class.java)!!)
                        var reviews = ArrayList<Review>()
                        if (it.result.get("reviews") != null) {
                            val reviewsMap = it.result.get("reviews") as List<Map<String, Any>>
                            reviews = convertMapToArrayList(reviewsMap)
                            val userReview = isUserReview(reviews, userEmail)
                            if (userReview != null) {
                                reviews.remove(userReview)
                            }
                        }
                        reviews.add(review)
                        movie.countRating()
                        movie.reviews = reviews
                        db.collection("movies").document(movieDocName).set(movie)
                            .addOnCompleteListener {
                                finish()
                                Toast.makeText(this, "Отзыв сохранен", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
        }
    }

    private fun convertMapToArrayList(reviewsMap: List<Map<String, Any>>): ArrayList<Review> {
        val reviews = ArrayList<Review>()
        for (reviewMap in reviewsMap) {
            reviews.add(
                Review(
                    reviewMap["author"].toString(),
                    reviewMap["authorEmail"].toString(),
                    reviewMap["text"].toString(),
                    reviewMap["createdDate"].toString(),
                    (reviewMap["rating"] as Number).toFloat()
                )
            )
        }
        return reviews
    }

    private fun isUserReview(reviews: List<Review>, email: String): Review? {
        for (review in reviews) {
            if (review.authorEmail == email) {
                return review
            }
        }
        return null
    }
}