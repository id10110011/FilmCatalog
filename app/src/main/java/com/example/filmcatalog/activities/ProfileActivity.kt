package com.example.filmcatalog.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.filmcatalog.databinding.ActivityProfileBinding
import com.example.filmcatalog.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var collectionReference: CollectionReference

    private lateinit var userEmail: String
    private lateinit var user: User
    private val collectionName = "users"
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        collectionReference = db.collection(collectionName)

        fillProfile()
        setListeners()
    }

    private fun saveUser() {
        val name = binding.profileFirstname.text.toString()
        val lastname = binding.profileLastname.text.toString()
        val dateBirth = binding.profileBirthDate.text.toString()
        val description = binding.profileDescription.text.toString()

        val savedUser = User(user.email, user.password, name, lastname, dateBirth, description)
        collectionReference.document(userEmail).set(savedUser)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Профиль успешно обновлен", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun setListeners() {
        binding.navMenu.menuProfile.setCardBackgroundColor(Color.parseColor("#3F484A"))
        binding.editButton.setOnClickListener {
            saveUser()
        }
        binding.signoutButton.setOnClickListener {
            signOut()
        }

        binding.dateButton.setOnClickListener {
            showDatePicker()
        }
        binding.deleteUserButton.setOnClickListener {
            showSubmitDeleteUserDialog()
        }
        binding.navMenu.menuFavorites.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
            finish()
        }
        binding.navMenu.menuCatalog.setOnClickListener {
            startActivity(Intent(this, CatalogActivity::class.java))
            finish()
        }
    }

    private fun fillProfile() {
        userEmail = firebaseAuth.currentUser?.email.toString()
        binding.profileEmail.setText(userEmail)

        collectionReference.document(userEmail).get(Source.DEFAULT)
            .addOnCompleteListener {
                if (it.result.exists()) {
                    user = it.result.toObject(User::class.java)!!
                    binding.profileEmail.setText(user.email)
                    binding.profileFirstname.setText(user.firstname)
                    binding.profileLastname.setText(user.lastname)
                    binding.profileBirthDate.setText(user.dateOfBirth)
                    binding.profileDescription.setText(user.description)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(this,
        {   DatePicker, year: Int, month: Int, day: Int ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, day)
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN)
            val formattedDate = dateFormat.format(selectedDate.time)
            binding.profileBirthDate.setText(formattedDate)
        },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showSubmitDeleteUserDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Удаление аккаунта")
            .setMessage("Аккаут и все данные пользователя будут удалены. Удалить?")
            .setPositiveButton("Да") { dialog, which ->
                deleteUser()
            }
            .setNegativeButton("Нет") { dialog, which ->
                dialog.dismiss()
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun signOut() {
        firebaseAuth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }

    private fun deleteUser() {
        val user = firebaseAuth.currentUser!!

        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    collectionReference.document(userEmail).delete()
                        .addOnCompleteListener {
                            startActivity(Intent(this, LoginActivity::class.java))
                            finishAffinity()
                            Toast.makeText(this, "Пользователь успешно удален", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }
}