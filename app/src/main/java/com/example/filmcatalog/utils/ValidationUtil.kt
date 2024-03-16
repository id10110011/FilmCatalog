package com.example.filmcatalog.utils

import android.content.Context
import android.util.Patterns
import android.widget.Toast

class ValidationUtil {
    companion object Validation {
        fun validateEmail(context: Context, email: String): Boolean {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(context, "Некорректная электронная почта", Toast.LENGTH_SHORT).show()
                return false
            }
            if (email.length > 80) {
                Toast.makeText(context, "Длина почты должна быть не более 80 символов", Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }

        fun validateName(context: Context, name: String): Boolean {
            if (name.isEmpty()) {
                Toast.makeText(context, "Введите имя", Toast.LENGTH_SHORT).show()
                return false
            }
            if (name.length > 50) {
                Toast.makeText(context, "Длина имени должна быть не более 50 символов", Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }

        fun validatePassword(context: Context, password: String): Boolean {
            if (password.isEmpty()) {
                Toast.makeText(context, "Введите пароль", Toast.LENGTH_SHORT).show()
                return false
            }
            if (password.length < 6) {
                Toast.makeText(context, "Длина пароля должна быть не менее 6 символов", Toast.LENGTH_SHORT).show()
                return false
            }
            if (password.length > 40) {
                Toast.makeText(context, "Длина пароля должна быть не более 40 символов", Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }

        fun validatePasswords(context: Context, password: String, rePassword: String): Boolean {
            if (validatePassword(context, password)) {
                return false
            }
            if (password != rePassword) {
                Toast.makeText(context, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }
    }
}