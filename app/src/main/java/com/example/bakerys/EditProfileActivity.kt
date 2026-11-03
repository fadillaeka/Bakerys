@file:OptIn(
    io.github.jan.supabase.annotations.SupabaseExperimental::class,
    kotlinx.serialization.InternalSerializationApi::class
)
package com.example.bakerys

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import java.io.File
import java.io.FileOutputStream

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var imageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val localUri = saveImageToInternalStorage(uri)
            if (localUri != null) {
                imageUri = localUri
                binding.ivFotoProfil.setImageURI(localUri)
                Toast.makeText(this, "Foto berhasil dipilih!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadSavedProfileImage()
        loadProfileData()

        binding.btnUbahFoto.setOnClickListener { pickImageLauncher.launch("image/*") }
        binding.cardUbahFoto.setOnClickListener { pickImageLauncher.launch("image/*") }
        binding.btnSimpan.setOnClickListener { simpanPerubahanProfil() }
        binding.btnKembali.setOnClickListener { finish() }
    }

    private fun loadSavedProfileImage() {
        val sharedPref = getSharedPreferences("BakerysPrefs", Context.MODE_PRIVATE)
        val savedUriStr = sharedPref.getString("profile_image_uri", null)
        if (!savedUriStr.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(savedUriStr)
                imageUri = uri
                binding.ivFotoProfil.setImageURI(uri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): Uri? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val file = File(filesDir, "profile_image.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun loadProfileData() {
        lifecycleScope.launch {
            try {
                val currentUser = SupabaseClient.instance.auth.currentUserOrNull()
                if (currentUser != null) {
                    val userId = currentUser.id

                    val result = SupabaseClient.instance.from("users")
                        .select { filter { eq("id", userId) } }
                        .decodeList<JsonObject>()

                    if (result.isNotEmpty()) {
                        val userObj = result.first()
                        val namaLengkap = userObj["nama_lengkap"]?.jsonPrimitive?.content ?: ""
                        val username = userObj["username"]?.jsonPrimitive?.content ?: ""
                        val bio = userObj["bio"]?.jsonPrimitive?.content ?: ""

                        binding.etNamaLengkap.setText(namaLengkap)
                        binding.etUsername.setText(username)
                        binding.etBio.setText(bio)
                    } else {
                        binding.etUsername.setText(currentUser.email?.substringBefore("@") ?: "")
                    }

                    binding.etEmail.setText(currentUser.email ?: "")
                }
            } catch (e: Exception) {
                android.util.Log.e("EDIT_PROFILE_LOAD", "Error: ${e.message}")
            }
        }
    }

    private fun simpanPerubahanProfil() {
        val fullNameBaru = binding.etNamaLengkap.text.toString().trim()
        var usernameBaru = binding.etUsername.text.toString().trim()
        val bioBaru = binding.etBio.text.toString().trim()

        if (usernameBaru.isEmpty()) {
            usernameBaru = if (fullNameBaru.isNotEmpty()) fullNameBaru else "Baker"
        }

        // Simpan ke SharedPreferences Lokal (Instan)
        val sharedPref = getSharedPreferences("BakerysPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("profile_name", usernameBaru)
        editor.putString("profile_bio", bioBaru)
        if (imageUri != null) {
            editor.putString("profile_image_uri", imageUri.toString())
        }
        editor.apply()

        // Simpan ke Supabase DB
        lifecycleScope.launch {
            try {
                val currentUser = SupabaseClient.instance.auth.currentUserOrNull()
                if (currentUser != null) {
                    val userId = currentUser.id
                    val updatedData = buildJsonObject {
                        put("nama_lengkap", fullNameBaru)
                        put("username", usernameBaru)
                        put("bio", bioBaru)
                    }
                    SupabaseClient.instance.from("users").update(updatedData) {
                        filter { eq("id", userId) }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("EDIT_PROFILE_ERROR", "Error: ${e.message}")
            }

            Toast.makeText(this@EditProfileActivity, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}