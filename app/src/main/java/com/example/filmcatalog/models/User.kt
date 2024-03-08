package com.example.filmcatalog.models

class User {
    lateinit var email: String
    lateinit var password: String
    var firstname: String = ""
    var lastname: String = ""
    var dateOfBirth: String = ""
    var description: String = ""

    constructor()

    constructor(email: String, password: String, firstname: String,) {
        this.email = email
        this.password = password
        this.firstname = firstname
    }

    constructor(user: User) {
        this.email = user.email
        this.firstname = user.firstname
        this.password = user.password
    }

    constructor(
        email: String,
        password: String,
        firstname: String,
        lastname: String,
        dateOfBirth: String,
        description: String
    ) {
        this.email = email
        this.password = password
        this.firstname = firstname
        this.lastname = lastname
        this.dateOfBirth = dateOfBirth
        this.description = description
    }


}