package com.example.githubreposmvi.shared.auth

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser


sealed class AuthState {

    object Idle : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
    object Loading : AuthState()
    data class SuccessGoogle(val account: GoogleSignInAccount, val credential : AuthCredential) : AuthState()
    data class SuccessFacebook(val user: FirebaseUser) : AuthState()
    data class Error(val errorMessage: String) : AuthState()
}
