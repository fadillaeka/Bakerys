package com.example.bakerys

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TersimpanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tersimpan)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val rvTersimpan = findViewById<RecyclerView>(R.id.rvTersimpan)
        rvTersimpan.layoutManager = LinearLayoutManager(this)

        // Ambil daftar resep yang disukai / disimpan
        // (Bisa memanggil list global resep yang ber-status isBookmarked = true)
    }
}