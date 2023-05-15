package com.example.githubreposmvi.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubreposmvi.shared.auth.AuthIntent
import com.example.githubreposmvi.shared.auth.AuthState
import com.example.githubreposmvi.shared.Validation
import com.example.githubreposmvi.ui.main.MainActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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
                    is AuthIntent.SignInGoogle -> signInGoogle()
                    is AuthIntent.SignInFacebook -> signInFacebook(it.context, it.loginButton)
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
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        _authState.value = user?.let {
                            AuthState.Authenticated(it)
                        }
                            ?: AuthState.Error("User is null")
                    } else {
                        _authState.value =
                            AuthState.Error(task.exception?.message ?: "Unknown error")
                    }
                }
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
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        _authState.value = user?.let {
                            AuthState.Authenticated(user)
                        } ?: AuthState.Error("User is null")
                    } else {
                        _authState.value =
                            AuthState.Error(task.exception?.message ?: "Unknown error")
                    }
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }

    private fun signInGoogle() {
        _authState.value = AuthState.Loading
    }

    fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            val email = account?.email
            val name = account?.displayName
            val token = account?.idToken
            val credential = GoogleAuthProvider.getCredential(token, null)

            if (email != null && name != null) {
                _authState.value = AuthState.SuccessGoogle(account, credential)
            } else {
                _authState.value = AuthState.Error("Failed to retrieve account information")
            }
        } else {
            _authState.value = AuthState.Error(task.exception.toString())
        }
    }


    private fun signInFacebook(context: Activity, loginButton: LoginButton) {
        _authState.value = AuthState.Loading

        val callbackManager = CallbackManager.Factory.create()

        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)
            }

            override fun onCancel() {
                _authState.value = AuthState.Error("Login cancelled")
            }

            override fun onError(error: FacebookException) {
                _authState.value = AuthState.Error(error.message ?: "Unknown error")
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    _authState.value = user?.let { AuthState.SuccessFacebook(it) }
                        ?: AuthState.Error("User is null")
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Unknown error")
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