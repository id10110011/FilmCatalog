package com.example.filmcatalog.models

import java.io.Serializable

class User : Serializable {
    lateinit var email: String
    var firstname: String = ""
    var lastname: String = ""
    var dateOfBirth: String = ""
    var country: String = ""
    var city: String = ""
    var gender: String = ""
    var education: String = ""
    var description: String = ""

    constructor()

    constructor(email: String, firstname: String,) {
        this.email = email
        this.firstname = firstname
    }

    constructor(user: User) {
        this.email = user.email
        this.firstname = user.firstname
    }

    constructor(
        email: String,
        firstname: String,
        lastname: String,
        dateOfBirth: String,
        city: String,
        country: String,
        gender: String,
        education: String,
        description: String
    ) {
        this.email = email
        this.firstname = firstname
        this.lastname = lastname
        this.dateOfBirth = dateOfBirth
        this.city = city
        this.country = country
        this.gender = gender
        this.education = education
        this.description = description
    }
}