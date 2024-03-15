package com.example.filmcatalog.models

import java.io.Serializable

class Review : Serializable {
    lateinit var author: String
    lateinit var authorEmail: String
    lateinit var text: String
    lateinit var createdDate: String
    var rating: Float = 0.0f

    constructor()

    constructor(author: String, authorEmail: String, text: String, createdDate: String, rating: Float) {
        this.author = author
        this.authorEmail = authorEmail
        this.text = text
        this.createdDate = createdDate
        this.rating = rating
    }
}