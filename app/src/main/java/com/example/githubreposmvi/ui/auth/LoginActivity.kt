package com.example.githubreposmvi.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.githubreposmvi.R
import com.example.githubreposmvi.data.auth.AuthIntent
import com.example.githubreposmvi.data.auth.AuthState
import com.example.githubreposmvi.databinding.ActivityLoginBinding
import com.example.githubreposmvi.ui.main.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
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
        binding.root.isEnabled = true
        binding.signIn.isEnabled = true
        binding.register.isEnabled = true
        binding.progressBar.visibility = View.GONE
        binding.overlayView.visibility = View.GONE
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}