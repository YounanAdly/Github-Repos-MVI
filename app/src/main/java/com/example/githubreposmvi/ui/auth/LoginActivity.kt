package com.example.githubreposmvi.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.githubreposmvi.R
import com.example.githubreposmvi.data.auth.AuthIntent
import com.example.githubreposmvi.data.auth.AuthState
import com.example.githubreposmvi.databinding.ActivityLoginBinding
import com.example.githubreposmvi.ui.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
        setupGoogle()
    }

    //Setup UI
    private fun setupUi() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.signIn.setOnClickListener(this)
        binding.register.setOnClickListener(this)
        //State In ViewModel
        observeAuthState()
    }

    override fun onClick(v: View?) {
        when (v) {
            // Call Login
            binding.signIn -> {
                val email = binding.email.text.toString().trim()
                val password = binding.password.text.toString()
                lifecycleScope.launch {
                    viewModel.authIntent.send(AuthIntent.SignIn(email, password))
                }
            }
            // Start Register Activity
            binding.register -> startActivity(
                Intent(
                    this@LoginActivity,
                    RegisterActivity::class.java
                )
            )
        }
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            viewModel.authState.collect {
                when (it) {
                    is AuthState.Authenticated -> successMessage()
                    is AuthState.Error -> errorMessage(it.errorMessage)
                    is AuthState.Idle -> Log.d(TAG, "state: -> Idle ")
                    is AuthState.Loading -> handleLoadingState()
                    is AuthState.SuccessGoogle -> updateUI(it.account, it.credential)
                }
            }
        }
    }

    private fun successMessage() {
        resetUI()

        SweetAlertDialog(
            this,
            SweetAlertDialog.SUCCESS_TYPE
        ).setTitleText("Login Successfully")
            .setConfirmButton("Ok") {
                it.dismissWithAnimation()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .show()
    }

    private fun errorMessage(message: String) {
        resetUI()
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Error")
            .setContentText(message).show()
    }

    private fun handleLoadingState() {
        // Disable user interaction and show a loading indicator
        binding.root.isEnabled = false
        binding.signIn.isEnabled = false
        binding.register.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
        binding.overlayView.visibility = View.VISIBLE
    }

    private fun resetUI() {
        binding.signIn.isEnabled = true
        binding.root.isEnabled = true
        binding.register.isEnabled = true
        binding.progressBar.visibility = View.GONE
        binding.overlayView.visibility = View.GONE
    }

    private fun setupGoogle() {

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.googleSignIn.setOnClickListener {
            signInGoogle()
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
        lifecycleScope.launch {
            viewModel.authIntent.send(AuthIntent.SignInGoogle)
        }

    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                viewModel.handleSignInResult(task)
            }
        }


    private fun updateUI(account: GoogleSignInAccount, credential: AuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("email", account.email)
                intent.putExtra("name", account.displayName)
                startActivity(intent)
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}