package com.example.githubreposmvi.ui

import android.animation.Animator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.githubreposmvi.R
import com.example.githubreposmvi.databinding.ActivitySplashBinding
import com.example.githubreposmvi.ui.auth.LoginActivity
import com.example.githubreposmvi.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        setupUI()
    }

    private fun setupUI() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        binding.animationView.setAnimation("github.json")

        binding.animationView.addAnimatorListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(p0: Animator) {
                Log.d(TAG, "onAnimationStart: ")
            }

            override fun onAnimationEnd(p0: Animator) {
                checkUserAuthentication()
            }

            override fun onAnimationCancel(p0: Animator) {
                Log.d(TAG, "onAnimationCancel: ")
            }

            override fun onAnimationRepeat(p0: Animator) {
                Log.d(TAG, "onAnimationRepeat: ")
            }

        })
    }

    private fun checkUserAuthentication() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is already signed in
            proceedToHomeScreen()
        } else {
            // User is not signed in
            showLoginScreen()
        }
    }

    private fun proceedToHomeScreen() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finishAffinity()
    }

    private fun showLoginScreen() {
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }

    companion object {
        private const val TAG = "SplashActivity"
    }
}