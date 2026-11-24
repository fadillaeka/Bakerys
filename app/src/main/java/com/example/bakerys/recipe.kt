package com.example.bakerys

import android.net.Uri

data class Recipe(
    val id: Int,
    val title: String,
    val description: String,
    val imageResId: Int? = null,
    val imageUri: Uri? = null,
    var isLiked: Boolean = false,
    var isBookmarked: Boolean = false,
    var likeCount: Int = 0
)
