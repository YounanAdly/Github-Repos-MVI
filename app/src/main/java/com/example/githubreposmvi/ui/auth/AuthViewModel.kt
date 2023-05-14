package com.example.githubreposmvi.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubreposmvi.data.auth.AuthIntent
import com.example.githubreposmvi.data.auth.AuthState
import com.example.githubreposmvi.shared.Validation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    val authIntent = Channel<AuthIntent> { Channel.UNLIMITED }
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> get() = _authState

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            authIntent.consumeAsFlow().collect {
                when (it) {
                    is AuthIntent.SignIn -> signIn(it.email, it.password)
                    is AuthIntent.SignUp -> signUp(it.email, it.password, it.confirmPassword)
                }
            }
        }
    }

    private fun signIn(email: String, password: String) {
        _authState.value = AuthState.Loading
        // Check Email Format
        if (!Validation.isEmailValid(email)) {
            _authState.value = AuthState.Error("Invalid email address")
            resetAuthStateAfterDelay()
            return
        }

        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }

    private fun signUp(email: String, password: String, confirmPassword: String) {
        _authState.value = AuthState.Loading
        // Check Email Format
        if (!Validation.isEmailValid(email)) {
            _authState.value = AuthState.Error("Invalid email address")
            resetAuthStateAfterDelay()
            return
        }
        // Check Password Matches
        if (!Validation.isPasswordValid(password, confirmPassword)) {
            _authState.value = AuthState.Error("Password Doesn't match")
            resetAuthStateAfterDelay()
            return
        }

        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }


    // Reset State After 3 Seconds
    private fun resetAuthStateAfterDelay() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000) // Delay for 3 seconds
            _authState.value = AuthState.Idle // Reset to Idle state
        }
    }
}