package com.example.githubreposmvi.shared

import android.util.Patterns

object Validation {

     fun isEmailValid(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    fun isPasswordValid(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }
}