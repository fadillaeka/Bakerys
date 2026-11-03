package com.example.bakerys

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Cari tombol Login dan Daftar di layout welcome
        val btnLogin = findViewById<Button>(R.id.btnLoginWelcome)
        val btnDaftar = findViewById<Button>(R.id.btnDaftarWelcome)

        // Klik Login -> Masuk ke MainActivity (atau LoginActivity jika ada)
        btnLogin?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Klik Daftar -> Masuk ke Register Activity
        btnDaftar?.setOnClickListener {
            // Sesuaikan nama Activity pendaftaran kamu (misal: RegisterActivity)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}