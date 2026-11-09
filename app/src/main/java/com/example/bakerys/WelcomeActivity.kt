package com.example.bakerys

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlin.jvm.java
import com.example.bakerys.RegisterActivity


@kotlinx.serialization.InternalSerializationApi
class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val btnLogin = findViewById<View>(R.id.btnLoginWelcome)
        val btnDaftar = findViewById<View>(R.id.btnDaftarWelcome)

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