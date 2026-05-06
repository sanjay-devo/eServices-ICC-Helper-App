package com.icc.eserviceshelper

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.icc.eserviceshelper.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load logo dynamically from URL
        val logoUrl = "https://indiacybercafe.com/wp-content/uploads/2026/05/eServices-ICC-Helper-App-Icon.webp"
        
        Glide.with(this)
            .load(logoUrl)
            .placeholder(R.mipmap.ic_launcher) // Show default icon while loading
            .error(R.mipmap.ic_launcher)       // Show default icon if load fails
            .into(binding.ivLogo)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2500) // Increased slightly to allow image loading time
    }
}
