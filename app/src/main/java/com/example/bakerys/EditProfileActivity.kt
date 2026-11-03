package com.example.bakerys

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bakerys.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSimpan.setOnClickListener {
            Toast.makeText(this, "Perubahan profil disimpan", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnKembali.setOnClickListener {
            finish()
        }
    }
}