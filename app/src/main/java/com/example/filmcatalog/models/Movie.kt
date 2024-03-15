package com.example.filmcatalog.models

class Movie {
    lateinit var name: String
    lateinit var description: String
    lateinit var pictureNames: ArrayList<String>
    var reviews = ArrayList<Review>()
    var rating: Float = 0.0f


    constructor()

    constructor(name: String, description: String, pictureNames: ArrayList<String>) {
        this.name = name
        this.description = description
        this.pictureNames = pictureNames
    }

    constructor(movie: Movie) {
        this.name = movie.name
        this.description = movie.description
        this.pictureNames = movie.pictureNames
        this.reviews = movie.reviews
    }

    constructor(
        name: String,
        description: String,
        pictureNames: ArrayList<String>,
        reviews: ArrayList<Review>
    ) {
        this.name = name
        this.description = description
        this.pictureNames = pictureNames
        this.reviews = reviews
        this.rating = countRating(reviews)
    }

    private fun countRating(reviews: ArrayList<Review>): Float {
        return ((reviews.sumOf { it.rating.toDouble() } / reviews.size).toFloat())
    }
}