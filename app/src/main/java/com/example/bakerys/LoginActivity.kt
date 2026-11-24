package com.example.bakerys

import android.content.Context
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
                var userEmail = ""

                if (usernameInput.contains("@")) {
                    // Jika pengguna mengetik email langsung
                    userEmail = usernameInput
                } else {
                    // Cari email di tabel 'users' berdasarkan username
                    try {
                        val result = SupabaseClient.instance.from("users")
                            .select {
                                filter {
                                    eq("username", usernameInput)
                                }
                            }.decodeList<JsonObject>()

                        if (result.isNotEmpty()) {
                            userEmail = result.first()["email"]?.jsonPrimitive?.content ?: ""
                        }
                    } catch (eUsers: Exception) {
                        android.util.Log.e("LOGIN_USERS_QUERY", "Catatan query users: ${eUsers.message}")
                    }

                    // Cek juga di SharedPreferences jika tersimpan lokal
                    if (userEmail.isEmpty()) {
                        val savedName = getSharedPreferences("BakerysPrefs", Context.MODE_PRIVATE).getString("profile_name", null)
                        val savedEmail = getSharedPreferences("BakerysPrefs", Context.MODE_PRIVATE).getString("profile_email", null)
                        if (savedName.equals(usernameInput, ignoreCase = true) && !savedEmail.isNullOrEmpty()) {
                            userEmail = savedEmail
                        }
                    }
                }

                if (userEmail.isEmpty()) {
                    Toast.makeText(this@LoginActivity, "Username/Email tidak ditemukan!", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // 2. Login ke Supabase Auth menggunakan email dan password
                SupabaseClient.instance.auth.signInWith(Email) {
                    email = userEmail
                    password = passwordInput
                }

                // Periksa apakah user diblokir oleh moderator di tabel 'users'
                try {
                    val userCheck = SupabaseClient.instance.from("users").select {
                        filter {
                            eq("email", userEmail)
                        }
                    }.decodeList<JsonObject>()

                    val userObj = userCheck.firstOrNull()
                    val userRole = userObj?.get("role")?.jsonPrimitive?.content ?: "user"
                    val dbUsername = userObj?.get("username")?.jsonPrimitive?.content ?: ""

                    if (userRole.equals("blocked", ignoreCase = true) || userRole.equals("nonaktif", ignoreCase = true)) {
                        SupabaseClient.instance.auth.signOut()
                        Toast.makeText(this@LoginActivity, "Akun Anda telah dinonaktifkan / diblokir oleh Moderator.", Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    // Tentukan hak akses admin
                    val isAdmin = userRole.equals("admin", ignoreCase = true) ||
                                  dbUsername.equals("belanjasapi", ignoreCase = true) ||
                                  dbUsername.equals("wapa", ignoreCase = true) ||
                                  usernameInput.equals("belanjasapi", ignoreCase = true) ||
                                  usernameInput.equals("wapa", ignoreCase = true)

                    val sharedPref = getSharedPreferences("BakerysPrefs", Context.MODE_PRIVATE)
                    sharedPref.edit()
                        .putBoolean("is_admin", isAdmin)
                        .putString("profile_role", userRole)
                        .apply()
                } catch (eCheck: Exception) {
                    android.util.Log.e("LOGIN_BLOCK_CHECK", "Gagal cek status role: ${eCheck.message}")
                }

                // Simpan profil nama ke SharedPreferences
                val sharedPref = getSharedPreferences("BakerysPrefs", Context.MODE_PRIVATE)
                val displayName = if (usernameInput.contains("@")) usernameInput.substringBefore("@") else usernameInput
                val authUserId = SupabaseClient.instance.auth.currentUserOrNull()?.id
                val editor = sharedPref.edit()
                    .putString("profile_name", displayName)
                    .putString("profile_email", userEmail)
                if (!authUserId.isNullOrEmpty()) {
                    editor.putString("current_user_id", authUserId)
                }
                editor.apply()

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