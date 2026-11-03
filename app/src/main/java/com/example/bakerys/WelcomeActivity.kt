package com.example.bakerys

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Gunakan View biasa agar tidak crash terlepas apapun tipe button-nya (Button / MaterialButton)
        val btnLogin = findViewById<View>(R.id.btnLoginWelcome)
        val btnDaftar = findViewById<View>(R.id.btnDaftarWelcome)

        // Klik Login -> Masuk ke MainActivity
        btnLogin?.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Klik Daftar -> Masuk ke Register Activity (atau MainActivity)
        btnDaftar?.setOnClickListener {
            // Sesuaikan jika punya RegisterActivity, jika belum pakai MainActivity dulu
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}