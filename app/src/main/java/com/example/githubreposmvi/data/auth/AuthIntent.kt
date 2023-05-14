package com.example.githubreposmvi.data.auth

sealed class AuthIntent {

    data class SignIn(val email: String, val password: String) : AuthIntent()
    data class SignUp(val email: String, val password: String,val confirmPassword: String) : AuthIntent()
}