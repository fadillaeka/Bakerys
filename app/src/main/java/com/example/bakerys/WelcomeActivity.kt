package com.example.bakerys

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

@kotlinx.serialization.InternalSerializationApi
class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val ivLogo = findViewById<ImageView>(R.id.ivLogoWelcome)
        val tvQuote = findViewById<TextView>(R.id.tvQuote)
        val btnLogin = findViewById<View>(R.id.btnLoginWelcome)
        val btnDaftar = findViewById<View>(R.id.btnDaftarWelcome)

        // 1. Animasi Logo muncul perlahan (Fade In + Zoom)
        ivLogo?.apply {
            alpha = 0f
            scaleX = 0.8f
            scaleY = 0.8f
            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(700)
                .start()
        }

        // 2. Animasi Teks Slogan meluncur naik dari bawah
        tvQuote?.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(200)
                .setDuration(600)
                .start()
        }

        // 3. Animasi Tombol Login meluncur naik
        btnLogin?.apply {
            alpha = 0f
            translationY = 60f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(350)
                .setDuration(600)
                .start()
        }

        // 4. Animasi Tombol Daftar meluncur naik setelah Login
        btnDaftar?.apply {
            alpha = 0f
            translationY = 60f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(450)
                .setDuration(600)
                .start()
        }

        // Klik Login -> Masuk ke LoginActivity
        btnLogin?.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Klik Daftar -> Masuk ke RegisterActivity
        btnDaftar?.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}