package com.example.bakerys

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bakerys.databinding.ActivityRegisterBinding // Sesuaikan jika pakai ViewBinding
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@kotlinx.serialization.InternalSerializationApi
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Event saat tombol Daftar diklik
        binding.btnDaftarSubmit.setOnClickListener {
            val email = binding.etEmailReg.text.toString().trim()
            val password = binding.etPasswordReg.text.toString().trim()
            val username = binding.etUsernameReg.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                daftarAkun(email, password, username)
            } else {
                Toast.makeText(this, "Semua field wajib diisi!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun daftarAkun(emailInput: String, passwordInput: String, usernameInput: String) {
        lifecycleScope.launch {
            try {
                // 1. Sign Up ke Supabase Auth
                SupabaseClient.instance.auth.signUpWith(Email) {
                    email = emailInput
                    password = passwordInput
                }

                val userId = SupabaseClient.instance.auth.currentUserOrNull()?.id

                if (userId != null) {
                    // 2. Simpan Data Profil ke Tabel 'users' Supabase
                    val userProfile = buildJsonObject {
                        put("id", userId)
                        put("username", usernameInput)
                        put("email", emailInput)
                        put("password", passwordInput)
                    }
                    SupabaseClient.instance.from("users").insert(userProfile)

                    Toast.makeText(this@RegisterActivity, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                    finish() // Tutup halaman register & pindah ke Login
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Gagal daftar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}