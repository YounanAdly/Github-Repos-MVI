package com.example.githubreposmvi.data.auth

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential


sealed class AuthState {

    object Idle : AuthState()
    object Authenticated : AuthState()
    object Loading : AuthState()
    data class SuccessGoogle(val account: GoogleSignInAccount, val credential : AuthCredential) : AuthState()
    data class Error(val errorMessage: String) : AuthState()
}
