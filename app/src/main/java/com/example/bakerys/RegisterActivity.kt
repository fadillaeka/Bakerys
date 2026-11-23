package com.example.bakerys

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bakerys.databinding.ActivityRegisterBinding
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
                // 1. Sign Up ke Supabase Auth (Mendaftarkan akun resmi)
                SupabaseClient.instance.auth.signUpWith(Email) {
                    email = emailInput
                    password = passwordInput
                }

                val userId = SupabaseClient.instance.auth.currentUserOrNull()?.id ?: java.util.UUID.randomUUID().toString()

                // 2. Simpan Data Profil ke Tabel 'users' Supabase
                try {
                    val userProfile = buildJsonObject {
                        put("id", userId)
                        put("username", usernameInput)
                        put("email", emailInput)
                        // JANGAN simpan password plaintext di tabel!
                    }
                    SupabaseClient.instance.from("users").insert(userProfile)
                } catch (eUsers: Exception) {
                    Log.e("REGISTER_USERS_TABLE", "Catatan insert tabel users: ${eUsers.message}")
                }

                // 3. Simpan data profil ke Preferences lokal
                getSharedPreferences("BakerysPrefs", Context.MODE_PRIVATE).edit()
                    .putString("profile_name", usernameInput)
                    .putString("profile_email", emailInput)
                    .putString("current_user_id", userId)
                    .apply()

                Toast.makeText(this@RegisterActivity, "Registrasi berhasil! Silakan masuk.", Toast.LENGTH_SHORT).show()
                finish() // Tutup halaman register & pindah ke Login
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Gagal daftar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}