package com.example.filmcatalog.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.filmcatalog.databinding.ActivitySignupBinding
import com.example.filmcatalog.models.User
import com.example.filmcatalog.utils.ValidationUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private val collectionName = "users"

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var db: FirebaseFirestore
    private lateinit var collectionRef: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        collectionRef = db.collection(collectionName)

        binding.signupButton.setOnClickListener {
            createUser()
        }

        binding.signupRedirectLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun createUser() {
        val email = binding.signupEmail.text.toString()
        val name = binding.signupName.text.toString()
        val password = binding.signupPassword.text.toString()
        val rePassword = binding.signupRepassword.text.toString()

        if (ValidationUtil.validateEmail(this, email) &&
            ValidationUtil.validateName(this, name) &&
            ValidationUtil.validatePasswords(this, password, rePassword))
        {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {task ->
                    if (task.isSuccessful) {
                        val user = User(email, name)
                        collectionRef.document(user.email).set(user)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(this, "Пользователь успешно зарегистрирован", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
        }
    }
}