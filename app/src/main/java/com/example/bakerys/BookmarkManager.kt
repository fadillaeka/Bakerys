package com.example.bakerys

object BookmarkManager {
    val bookmarkedList = mutableListOf<Recipe>()

    fun addBookmark(recipe: Recipe) {
        if (!bookmarkedList.any { it.id == recipe.id || it.title == recipe.title }) {
            bookmarkedList.add(recipe)
        }
    }

    fun removeBookmark(recipe: Recipe) {
        bookmarkedList.removeAll { it.id == recipe.id || it.title == recipe.title }
    }

    fun isBookmarked(recipe: Recipe): Boolean {
        return bookmarkedList.any { it.id == recipe.id || it.title == recipe.title }
    }
}