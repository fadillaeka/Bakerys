package com.example.bakerys

import kotlinx.serialization.Serializable

@Serializable
data class Kategori(
    val id: Long? = null,
    val nama_kategori: String
)
