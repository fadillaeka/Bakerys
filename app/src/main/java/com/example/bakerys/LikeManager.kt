package com.example.bakerys

object LikeManager {
    val likedList = mutableListOf<Recipe>()

    fun addLike(recipe: Recipe) {
        if (!likedList.any { it.id == recipe.id || it.title == recipe.title }) {
            likedList.add(recipe)
        }
    }

    fun removeLike(recipe: Recipe) {
        likedList.removeAll { it.id == recipe.id || it.title == recipe.title }
    }

    fun isLiked(recipe: Recipe): Boolean {
        return likedList.any { it.id == recipe.id || it.title == recipe.title }
    }
}