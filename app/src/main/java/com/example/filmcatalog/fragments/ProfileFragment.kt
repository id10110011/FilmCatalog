package com.example.filmcatalog.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import com.example.filmcatalog.activities.EditProfileActivity
import com.example.filmcatalog.activities.LoginActivity
import com.example.filmcatalog.databinding.FragmentProfileBinding
import com.example.filmcatalog.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var collectionReference: CollectionReference

    private lateinit var userEmail: String
    private lateinit var user: User
    private val collectionUsers = "users"
    private val collectionFavorites = "favorites"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentProfileBinding.inflate(layoutInflater)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        collectionReference = db.collection(collectionUsers)

        setListeners()
    }

    override fun onResume() {
        super.onResume()
        fillProfile()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private fun setListeners() {
        binding.editButton.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }
        binding.signoutButton.setOnClickListener {
            showSubmitSignOutDialog()
        }
        binding.deleteUserButton.setOnClickListener {
            showSubmitDeleteUserDialog()
        }
    }

    private fun fillProfile() {
        userEmail = firebaseAuth.currentUser?.email.toString()

        collectionReference.document(userEmail).get(Source.DEFAULT)
            .addOnCompleteListener {
                if (it.result.exists()) {
                    user = it.result.toObject(User::class.java)!!
                    binding.profileEmail.text = user.email
                    binding.profileFirstname.text = user.firstname
                    binding.profileLastname.text = user.lastname
                    binding.profileBirthDate.text = user.dateOfBirth
                    binding.profileCity.text = user.city
                    binding.profileCounty.text = user.country
                    binding.profileGender.text = user.gender
                    binding.profileEducation.text = user.education
                    binding.profileDescription.text = user.description
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
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

    private fun showSubmitSignOutDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Выход из аккаунт")
            .setMessage("Выйти из аккаунта?")
            .setPositiveButton("Да") { dialog, which ->
                signOut()
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