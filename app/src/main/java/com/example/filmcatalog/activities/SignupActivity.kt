package com.example.filmcatalog.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.filmcatalog.databinding.ActivitySignupBinding
import com.example.filmcatalog.models.User
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

        if (validateEmail(email) &&
            validateName(name) &&
            validatePasswords(password, rePassword))
        {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {task ->
                    if (task.isSuccessful) {
                        val user = User(email, password, name)
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

    private fun validateEmail(email: String): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Некорректная электронная почта", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun validateName(name: String): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun validatePasswords(password: String, rePassword: String): Boolean {
        if (password.isEmpty()) {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != rePassword) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}