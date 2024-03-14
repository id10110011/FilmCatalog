package com.example.filmcatalog.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.filmcatalog.R
import com.example.filmcatalog.databinding.ActivityEditProfileBinding
import com.example.filmcatalog.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var collectionReference: CollectionReference

    private lateinit var genderValues: Array<String>
    private lateinit var user: User
    private val collectionUsers = "users"
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        collectionReference = db.collection(collectionUsers)
        genderValues = resources.getStringArray(R.array.gender_values)

        fillProfile()
        setListeners()
    }

    private fun fillProfile() {
        val intent = this.intent
        if (intent != null) {
            user = intent.getSerializableExtra("user") as User
            binding.editProfileEmail.setText(user.email)
            binding.editProfileFirstname.setText(user.firstname)
            binding.editProfileLastname.setText(user.lastname)
            binding.editProfileBirthDate.setText(user.dateOfBirth)
            binding.editProfileCity.setText(user.city)
            binding.editProfileCounty.setText(user.country)
            binding.editProfileGender.setSelection(genderValues.indexOf(user.gender))
            binding.editProfileEducation.setText(user.education)
            binding.editProfileDescription.setText(user.description)
        }
    }

    private fun saveUser() {
        val name = binding.editProfileFirstname.text.toString()
        val lastname = binding.editProfileLastname.text.toString()
        val dateBirth = binding.editProfileBirthDate.text.toString()
        val city = binding.editProfileCity.text.toString()
        val country = binding.editProfileCounty.text.toString()
        val gender = binding.editProfileGender.selectedItem.toString()
        val education = binding.editProfileEducation.text.toString()
        val description = binding.editProfileDescription.text.toString()


        val savedUser = User(
            user.email,
            user.password,
            name,
            lastname,
            dateBirth,
            city,
            country,
            gender,
            education,
            description
        )
        collectionReference.document(user.email).set(savedUser)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Профиль успешно обновлен", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { DatePicker, year: Int, month: Int, day: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, day)
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN)
                val formattedDate = dateFormat.format(selectedDate.time)
                binding.editProfileBirthDate.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun setListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.saveButton.setOnClickListener {
            saveUser()
            finish()
        }
        binding.dateButton.setOnClickListener {
            showDatePicker()
        }
    }
}