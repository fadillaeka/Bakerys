package com.example.bakerys

import android.net.Uri

// Model khusus untuk Postingan Biasa (bukan resep)
data class PostinganModel(
    val judul: String,
    val deskripsi: String,
    val imageUri: Uri? = null,
    val imageResId: Int? = null,
    val waktu: String = "Baru saja",
    val user: String = "Saya"
)

object PostinganRepository {
    // List penampung Postingan Publik (untuk Jelajah)
    val daftarJelajah = mutableListOf<PostinganModel>()

    // List penampung Draft
    val daftarDraft = mutableListOf<PostinganModel>()

    fun tambahKeJelajah(post: PostinganModel) {
        daftarJelajah.add(0, post) // Masukkan ke paling atas
    }

    fun tambahKeDraft(post: PostinganModel) {
        daftarDraft.add(0, post)
    }
}