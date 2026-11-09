package com.example.bakerys

import android.net.Uri

// Model khusus untuk Postingan Biasa
data class PostinganModel(
    val id: Int = System.currentTimeMillis().toInt(),
    val judul: String,
    val deskripsi: String = "",
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
        val index = daftarDraft.indexOfFirst { it.id == post.id }
        if (index != -1) {
            // Update draft yang sudah ada
            daftarDraft[index] = post
        } else {
            // Tambah draft baru di paling atas
            daftarDraft.add(0, post)
        }
    }

    fun hapusDraftById(id: Int) {
        daftarDraft.removeAll { it.id == id }
    }
}