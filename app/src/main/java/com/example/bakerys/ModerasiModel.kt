package com.example.bakerys

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KontenModerasi(
    @SerialName("id")
    val id: String,
    @SerialName("raw_id")
    val rawId: Long? = null,
    @SerialName("tipe")
    val tipe: String,
    @SerialName("judul")
    val judul: String,
    @SerialName("deskripsi")
    val deskripsi: String,
    @SerialName("status")
    val status: String = "aktif",
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class UserModerasi(
    @SerialName("id")
    val id: String,
    @SerialName("username")
    val username: String,
    @SerialName("nama_lengkap")
    val namaLengkap: String? = null,
    @SerialName("email")
    val email: String,
    @SerialName("role")
    val role: String = "user",
    @SerialName("foto_profil")
    val fotoProfil: String? = null
)
