package com.example.bakerys

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
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

            if (passwordBaru.isEmpty() || konfirmasiPassword.isEmpty()) {
                Toast.makeText(this, "Password baru tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordBaru != konfirmasiPassword) {
                Toast.makeText(this, "Konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tampilkan Popup Kustom saat Berhasil
            tampilkanPopupSukses()
        }
    }

    private fun tampilkanPopupSukses() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_succes_password)
        dialog.setCancelable(false)

        // Supaya background sudut dialog melengkung rapi (transparan di luar card)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnKembali = dialog.findViewById<Button>(R.id.btnKembaliKeLogin)
        btnKembali.setOnClickListener {
            dialog.dismiss()
            // Pindah kembali ke halaman Login dan hapus riwayat activity sebelumnya
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finish()
        }

        dialog.show()
    }
}