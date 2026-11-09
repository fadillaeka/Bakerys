package com.example.bakerys

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

@kotlinx.serialization.InternalSerializationApi
class TersimpanActivity : AppCompatActivity() {

    private val bookmarkedList = mutableListOf<Recipe>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tersimpan)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val rvTersimpan = findViewById<RecyclerView>(R.id.rvTersimpan)
        rvTersimpan.layoutManager = LinearLayoutManager(this)

        val adapter = TersimpanAdapter(
            bookmarkedList = bookmarkedList,
            onItemClick = { resep ->
                // Mengirimkan judul resep menggunakan key EXTRA_JUDUL agar dibaca oleh DetailResepActivity
                val intent = Intent(this, DetailResepActivity::class.java).apply {
                    putExtra("EXTRA_JUDUL", resep.title)
                    if (resep.imageResId != null) {
                        putExtra("EXTRA_GAMBAR", resep.imageResId)
                    }
                }
                startActivity(intent)
            },
            onRemoveBookmark = { resep, position ->
                if (position in 0 until bookmarkedList.size) {
                    bookmarkedList.removeAt(position)
                    rvTersimpan.adapter?.notifyItemRemoved(position)
                    rvTersimpan.adapter?.notifyItemRangeChanged(position, bookmarkedList.size - position)
                }
            }
        )

        rvTersimpan.adapter = adapter
    }
}