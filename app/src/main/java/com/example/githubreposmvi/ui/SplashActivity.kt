package com.example.githubreposmvi.ui

import android.animation.Animator
import android.content.Intent
import android.database.DatabaseUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.githubreposmvi.R
import com.example.githubreposmvi.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }

            override fun onAnimationCancel(p0: Animator) {
                Log.d(TAG, "onAnimationCancel: ")
            }

            override fun onAnimationRepeat(p0: Animator) {
                Log.d(TAG, "onAnimationRepeat: ")
            }

        })
    }

    companion object {
        private const val TAG = "SplashActivity"
    }
}