package com.example.bakerys

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bakerys.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSimpanPassword.setOnClickListener {
            val email = binding.etResetEmail.text.toString().trim()
            val passwordBaru = binding.etPasswordBaru.text.toString().trim()
            val konfirmasiPassword = binding.etKonfirmasiPassword.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Email harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordBaru != konfirmasiPassword) {
                Toast.makeText(this, "Konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Password Berhasil Diperbarui!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}