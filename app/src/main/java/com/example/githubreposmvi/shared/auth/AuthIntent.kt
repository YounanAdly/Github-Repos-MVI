package com.example.githubreposmvi.shared.auth

import android.app.Activity
import com.facebook.login.LoginManager
import com.facebook.login.widget.LoginButton

sealed class AuthIntent {

    data class SignIn(val email: String, val password: String) : AuthIntent()
    data class SignUp(val email: String, val password: String,val confirmPassword: String) : AuthIntent()

    object SignInGoogle : AuthIntent()
    data class SignInFacebook(val context : Activity, val loginButton: LoginButton) : AuthIntent()

}