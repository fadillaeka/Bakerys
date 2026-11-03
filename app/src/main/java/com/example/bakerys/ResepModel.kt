package com.example.bakerys

data class ResepModel(
    val judul: String,
    val deskripsi: String,
    val gambar: Int,
    var isFavorite: Boolean = false,
    var isDisukai: Boolean = false
)