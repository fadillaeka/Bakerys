package com.example.bakerys

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.bakerys.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var imageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            binding.ivFotoProfil.setImageURI(uri)
            Toast.makeText(this, "Foto berhasil dipilih!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Muat foto tersimpan secara aman tanpa membuat aplikasi crash
        loadSavedImage()

        binding.btnUbahFoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.cardUbahFoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSimpan.setOnClickListener {
            if (imageUri != null) {
                val sharedPref = getSharedPreferences("BakerysPrefs", Context.MODE_PRIVATE)
                sharedPref.edit().putString("profile_image_uri", imageUri.toString()).apply()
            }
            Toast.makeText(this, "Perubahan profil disimpan", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnKembali.setOnClickListener {
            finish()
        }
    }

    private fun loadSavedImage() {
        val sharedPref = getSharedPreferences("BakerysPrefs", Context.MODE_PRIVATE)
        val savedUriStr = sharedPref.getString("profile_image_uri", null)
        if (!savedUriStr.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(savedUriStr)
                imageUri = uri
                val inputStream = contentResolver.openInputStream(uri)
                val drawable = Drawable.createFromStream(inputStream, uri.toString())
                if (drawable != null) {
                    binding.ivFotoProfil.setImageDrawable(drawable)
                }
            } catch (e: Exception) {
                // Mencegah crash jika izin file galeri dari emulator sudah kadaluarsa
                e.printStackTrace()
            }
        }
    }
    private fun cropAndZoomToSubject(bitmap: android.graphics.Bitmap): android.graphics.Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val cropSize = (kotlin.math.min(width, height) * 0.45).toInt()

        val startX = (width - cropSize) / 2
        val startY = ((height - cropSize) * 0.85).toInt().coerceIn(0, height - cropSize)

        return android.graphics.Bitmap.createBitmap(bitmap, startX, startY, cropSize, cropSize)
    }
}