package com.example.filmcatalog.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.filmcatalog.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.loginRedirectSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            if (validateEmail(email) && validatePassword(password)) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) {task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Вы вошли в аккаунт", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Некорректная электронная почта или пароль", Toast.LENGTH_SHORT).show()
                        }
                    }
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

    private fun validatePassword(password: String): Boolean {
        if (password.isEmpty()) {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}