package com.example.filmcatalog.models

class Movie {
    lateinit var name: String
    lateinit var description: String
    lateinit var pictureNames: ArrayList<String>

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
    }

}