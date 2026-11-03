package com.example.bakerys

data class Recipe(
    val id: Int,
    val title: String,
    val description: String,
    val imageResId: Int,
    var isLiked: Boolean = false,
    val comments: MutableList<CommentItem> = mutableListOf() // Daftar komentar spesifik tiap resep
)