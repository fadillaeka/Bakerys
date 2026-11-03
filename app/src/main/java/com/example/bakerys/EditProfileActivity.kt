package com.example.bakerys

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bakerys.databinding.ActivityEditProfileBinding
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Tampilkan data profil saat ini dari Supabase
        loadProfileData()

        // 2. Tombol Simpan Perubahan
        binding.btnSimpan.setOnClickListener {
            simpanPerubahanProfil()
        }

        // 3. Tombol Kembali
        binding.btnKembali.setOnClickListener {
            finish()
        }
    }

    private fun loadProfileData() {
        lifecycleScope.launch {
            try {
                val currentUser = SupabaseClient.instance.auth.currentUserOrNull()
                if (currentUser != null) {
                    val userId = currentUser.id

                    val result = SupabaseClient.instance.from("users")
                        .select {
                            filter {
                                eq("id", userId)
                            }
                        }.decodeList<JsonObject>()

                    if (result.isNotEmpty()) {
                        val userObj = result.first()
                        // Mengambil dari kolom 'nama_lengkap'
                        val namaLengkap = userObj["nama_lengkap"]?.jsonPrimitive?.content ?: ""
                        val username = userObj["username"]?.jsonPrimitive?.content ?: ""
                        val bio = userObj["bio"]?.jsonPrimitive?.content ?: ""

                        binding.etFullNameEdit.setText(namaLengkap)
                        binding.etUsernameEdit.setText(username)
                        binding.etBioEdit.setText(bio)
                    } else {
                        binding.etUsernameEdit.setText(
                            currentUser.email?.substringBefore("@") ?: ""
                        )
                    }

                    binding.etEmailEdit.setText(currentUser.email ?: "")
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@EditProfileActivity,
                    "Gagal memuat data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun simpanPerubahanProfil() {
        val fullNameBaru = binding.etFullNameEdit.text.toString().trim()
        val usernameBaru = binding.etUsernameEdit.text.toString().trim()
        val bioBaru = binding.etBioEdit.text.toString().trim()

        if (usernameBaru.isEmpty()) {
            Toast.makeText(this, "Username tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val currentUser = SupabaseClient.instance.auth.currentUserOrNull() ?: return@launch
                val userId = currentUser.id

                // Gunakan update() agar hanya kolom ini saja yang diubah di database
                val updatedData = buildJsonObject {
                    put("nama_lengkap", fullNameBaru)
                    put("username", usernameBaru)
                    put("bio", bioBaru)
                }

                SupabaseClient.instance.from("users").update(updatedData) {
                    filter {
                        eq("id", userId)
                    }
                }

                Toast.makeText(this@EditProfileActivity, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                android.util.Log.e("EDIT_PROFILE_ERROR", "Error: ${e.message}")
                Toast.makeText(this@EditProfileActivity, "Gagal memperbarui: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}