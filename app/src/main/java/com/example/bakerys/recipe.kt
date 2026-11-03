package com.example.bakerys

data class Recipe(
    val id: Int,
    val title: String,
    val description: String,
    val imageResId: Int,
    var isLiked: Boolean = false,
    var isBookmarked: Boolean = false, // Status tersimpan
    val comments: MutableList<CommentItem> = mutableListOf()
)