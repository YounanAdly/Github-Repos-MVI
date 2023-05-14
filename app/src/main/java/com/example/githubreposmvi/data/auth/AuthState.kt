package com.example.githubreposmvi.data.auth


sealed class AuthState {

    object Idle : AuthState()
    object Authenticated : AuthState()
    object Loading : AuthState()
    data class Error(val errorMessage: String) : AuthState()
}
