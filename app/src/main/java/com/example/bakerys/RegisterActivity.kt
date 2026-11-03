package com.example.bakerys

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Tombol Daftar di activity_register.xml
        val btnDaftar = findViewById<View>(R.id.btnDaftarSubmit)

        btnDaftar?.setOnClickListener {
            tampilkanPopUpBerhasil()
        }
    }

    private fun tampilkanPopUpBerhasil() {
        // Inflate layout dialog_daftar_berhasil.xml
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_daftar_berhasil, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Tombol Kembali Ke Login di dalam dialog
        val btnKembaliLogin = dialogView.findViewById<Button>(R.id.btnKembaliLogin)
        btnKembaliLogin?.setOnClickListener {
            dialog.dismiss()

            // Kembali ke halaman Login (LoginActivity / WelcomeActivity)
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        dialog.show()
    }
}