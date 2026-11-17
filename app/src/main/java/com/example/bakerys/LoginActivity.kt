package com.example.bakerys

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

@kotlinx.serialization.InternalSerializationApi
class LoginActivity : AppCompatActivity() {

    private lateinit var etLoginUsername: EditText
    private lateinit var etLoginPassword: EditText
    private lateinit var btnMasukLogin: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etLoginUsername = findViewById(R.id.etLoginUsername)
        etLoginPassword = findViewById(R.id.etLoginPassword)
        btnMasukLogin = findViewById(R.id.btnMasukLogin)

        btnMasukLogin.setOnClickListener {
            val username = etLoginUsername.text.toString().trim()
            val password = etLoginPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginDenganUsername(username, password)
            } else {
                Toast.makeText(this, "Masukkan username dan password!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginDenganUsername(usernameInput: String, passwordInput: String) {
        lifecycleScope.launch {
            try {
                // 1. Cari email di tabel 'users' berdasarkan username
                val result = SupabaseClient.instance.from("users")
                    .select {
                        filter {
                            eq("username", usernameInput)
                        }
                    }.decodeList<JsonObject>()

                if (result.isEmpty()) {
                    Toast.makeText(this@LoginActivity, "Username tidak ditemukan!", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Ambil string email dari hasil query
                val userEmail = result.first()["email"]?.jsonPrimitive?.content

                if (userEmail.isNullOrEmpty()) {
                    Toast.makeText(this@LoginActivity, "Email untuk username ini tidak valid!", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // 2. Login ke Supabase Auth menggunakan email dan password
                SupabaseClient.instance.auth.signInWith(Email) {
                    email = userEmail
                    password = passwordInput
                }

                Toast.makeText(this@LoginActivity, "Login berhasil!", Toast.LENGTH_SHORT).show()

                // 3. Tutup layar Login dengan kue penuh, lalu pindah ke Beranda tanpa kedipan
                val pastryCover = findViewById<PastryTransitionView>(R.id.pastryTransitionLogin)
                if (pastryCover != null) {
                    pastryCover.animateCover {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                            putExtra("FROM_LOGIN", true)
                        }
                        startActivity(intent)
                        @Suppress("DEPRECATION")
                        overridePendingTransition(0, 0)
                        finish()
                    }
                } else {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                        putExtra("FROM_LOGIN", true)
                    }
                    startActivity(intent)
                    finish()
                }

            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Login gagal: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}