package com.example.bakerys

import android.net.Uri

data class ResepModel(
    val judul: String,
    val deskripsi: String,
    val gambar: Int? = null,
    val imageUri: Uri? = null,
    var isFavorite: Boolean = false,
    var isDisukai: Boolean = false
)