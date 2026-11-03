package com.example.bakerys

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@kotlinx.serialization.InternalSerializationApi
@Serializable
data class ResepModel(
    @SerialName("id")
    val id: Long? = null,

    @SerialName("user_id")
    val userId: String? = null,

    @SerialName("kategori_id")
    val kategoriId: Long? = null,

    @SerialName("judul")
    val judul: String = "",

    @SerialName("deskripsi")
    val deskripsi: String = "",

    @SerialName("gambar")
    val gambar: Int,

    @SerialName("waktu_panggang")
    val waktuPanggang: Int? = null,

    @SerialName("porsi")
    val porsi: String? = null,

    @SerialName("status")
    val status: String = "published",

    var isFavorite: Boolean = false,
    val isDisukai: Boolean = false
)
