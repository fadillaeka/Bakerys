package com.example.bakerys

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Kategori(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("nama_kategori")
    val nama_kategori: String
)
