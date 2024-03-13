package com.example.filmcatalog.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.finishAffinity
import com.example.filmcatalog.activities.LoginActivity
import com.example.filmcatalog.databinding.FragmentProfileBinding
import com.example.filmcatalog.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var collectionReference: CollectionReference

    private lateinit var userEmail: String
    private lateinit var user: User
    private val collectionUsers = "users"
    private val collectionFavorites = "favorites"
    private val calendar = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentProfileBinding.inflate(layoutInflater)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        collectionReference = db.collection(collectionUsers)

        fillProfile()
        setListeners()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private fun saveUser() {
        val name = binding.profileFirstname.text.toString()
        val lastname = binding.profileLastname.text.toString()
        val dateBirth = binding.profileBirthDate.text.toString()
        val city = binding.profileCity.text.toString()
        val country = binding.profileCounty.text.toString()
        val education = binding.profileEducation.text.toString()
        val description = binding.profileDescription.text.toString()


        val savedUser = User(user.email, user.password, name, lastname, dateBirth, city, country, education, description)
        collectionReference.document(userEmail).set(savedUser)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(activity, "Профиль успешно обновлен", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun setListeners() {
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
                    binding.profileCity.setText(user.city)
                    binding.profileCounty.setText(user.country)
                    binding.profileEducation.setText(user.education)
                    binding.profileDescription.setText(user.description)
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(requireActivity(),
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
        val builder = AlertDialog.Builder(requireActivity())
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
        startActivity(Intent(activity, LoginActivity::class.java))
        finishAffinity(requireActivity())
    }

    private fun deleteUser() {
        val user = firebaseAuth.currentUser!!

        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    collectionReference.document(userEmail).delete()
                        .addOnCompleteListener {
                            if (task.isSuccessful) {
                                db.collection(collectionFavorites).document(userEmail).delete()
                                    .addOnCompleteListener {
                                        startActivity(Intent(activity, LoginActivity::class.java))
                                        finishAffinity(requireActivity())
                                        Toast.makeText(
                                            activity,
                                            "Пользователь успешно удален",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                }
            }
    }
}