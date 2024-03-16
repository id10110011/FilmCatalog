package com.example.filmcatalog.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.filmcatalog.R
import com.example.filmcatalog.adapters.ImageAdapter
import com.example.filmcatalog.adapters.ReviewsAdapter
import com.example.filmcatalog.databinding.ActivityMovieBinding
import com.example.filmcatalog.models.Movie
import com.example.filmcatalog.models.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.toObject

class MovieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieBinding
    private lateinit var pageChangeListener: ViewPager2.OnPageChangeCallback

    private val collectionFavorites = "favorites"
    private val collectionMovies = "movies"
    private lateinit var db: FirebaseFirestore
    private lateinit var collectionRefFavs: CollectionReference
    private lateinit var movieDocumentReference: DocumentReference

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var docName: String
    private lateinit var userEmail: String
    private lateinit var reviewsMap: List<Map<String, Any>>
    private lateinit var reviews: List<Review>
    private lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser == null) {
            firebaseAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            userEmail = firebaseAuth.currentUser!!.email.toString()
        }

        db = FirebaseFirestore.getInstance()
        collectionRefFavs = db.collection(collectionFavorites)

        reviews = ArrayList<Review>()
        initPage()
        setImageToButtonFavorite()
        setListeners()
    }

    override fun onResume() {
        super.onResume()

        db.collection(collectionMovies).document(docName).get(Source.DEFAULT)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    movie = it.result.toObject(Movie::class.java)!!
                    if (it.result.get("reviews") != null) {
                        reviewsMap = it.result.get("reviews") as List<Map<String, Any>>
                        reviews = convertMapToArrayList(reviewsMap)
                    }
                    setMovieRating()
                    setUserReview()
                }
            }
    }

    private fun setMovieRating() {
        binding.ratingAndReviewsContainer.movieRatingBar.rating = movie.rating
        binding.ratingAndReviewsContainer.movieRatingCount.text = reviews.size.toString()
        binding.ratingAndReviewsContainer.movieRatingText.text = movie.rating.toString()
    }

    private fun setUserReview() {
        val review = isUserReview(reviews, userEmail)
        if (review != null) {
            binding.rateMovieText.text = resources.getString(R.string.your_review)
            binding.rateMovieView.visibility = View.GONE
            binding.userReview.userReviewContainer.visibility = View.VISIBLE
            fillUserReview(review)
        } else {
            binding.rateMovieText.text = resources.getString(R.string.rate_movie)
            binding.rateMovieView.visibility = View.VISIBLE
            binding.userReview.userReviewContainer.visibility = View.GONE
        }
    }

    private fun fillUserReview(review: Review) {
        binding.userReview.reviewItemAuthor.text = review.author
        binding.userReview.reviewItemText.text = review.text
        binding.userReview.reviewItemRating.rating = review.rating
        binding.userReview.reviewItemCreatedDate.text = review.createdDate
    }

    private fun isUserReview(reviews: List<Review>, email: String): Review? {
        for (review in reviews) {
            if (review.authorEmail == email) {
                return review
            }
        }
        return null
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
        binding.rateMovieBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            startRateMovieActivity()
        }
        binding.rateMovieReview.setOnClickListener {
            startRateMovieActivity()
        }
        binding.userReview.reviewItemEdit.setOnClickListener {
            startRateMovieActivity()
        }
        binding.ratingAndReviewsContainer.showReviewsButton.setOnClickListener {
            val intent = Intent(this, ReviewsActivity::class.java)
            intent.putExtra("movieDocName", docName)
            startActivity(intent)
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

    private fun startRateMovieActivity() {
        val intent = Intent(this, RateMovieActivity::class.java)
        intent.putExtra("movieDoc", docName)
        intent.putExtra("text", binding.userReview.reviewItemText.text.toString())
        intent.putExtra("rating", binding.userReview.reviewItemRating.rating)
        startActivity(intent)
    }

    private fun setReferenceArray(refArray: ArrayList<DocumentReference>) {
        collectionRefFavs.document(userEmail).set(
            mapOf(
                "refArray" to refArray
            )
        )
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
            val pictureNames = if (pictures.size > 1) {
                pictures.subList(1, pictures.size)
            } else {
                ArrayList()
            }

            if (pictureNames.isEmpty()) {
                binding.moviePicsCaption.visibility = View.GONE
                binding.moviePicsContainer.visibility = View.GONE
            } else {
                binding.moviePicsCaption.visibility = View.VISIBLE
                binding.moviePicsContainer.visibility = View.VISIBLE
            }
            fillMovieFields(name!!, description!!, pictures)
            initAdapter(pictureNames)
            initSlider(pictureNames.size)
        }
    }

    private fun fillMovieFields(name: String, description: String, pictureNames: MutableList<String>) {
        binding.movieName.text = name
        binding.movieDescription.text = description
        if (pictureNames.isNotEmpty()) {
            Glide.with(this)
                .load(Uri.parse(pictureNames[0]))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(binding.movieIcon)
        }
    }

    private fun initAdapter(pictureNames: MutableList<String>) {
        val imageAdapter = ImageAdapter()
        binding.viewPager.adapter = imageAdapter
        imageAdapter.submitList(pictureNames)
    }

    private fun initSlider(size: Int) {
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(8, 0, 8, 0)
        }
        val dotsImage = Array(size) {
            ImageView(this)
        }
        dotsImage.forEach {
            it.setImageResource(
                R.drawable.non_active_dot
            )
            binding.viewPagerDots.addView(it, params)
        }

        if (size > 0) {
            dotsImage[0].setImageResource(R.drawable.active_dot)
        }
        initPageChangeListener(dotsImage)
        binding.viewPager.registerOnPageChangeCallback(pageChangeListener)
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
}