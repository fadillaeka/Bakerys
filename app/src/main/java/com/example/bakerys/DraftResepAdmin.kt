package com.example.bakerys

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DraftResep(
    @SerialName("id")
    val id: String? = null,
    @SerialName("user_id")
    val user_id: String,
    @SerialName("kategori_id")
    val kategori_id: Long? = null,
    @SerialName("judul")
    val judul: String? = null,
    @SerialName("deskripsi")
    val deskripsi: String? = null,
    @SerialName("gambar")
    val gambar: String? = null,
    @SerialName("waktu_panggang")
    val waktu_panggang: Int? = null,
    @SerialName("porsi")
    val porsi: String? = null,
    @SerialName("status")
    val status: String? = "draft",
    @SerialName("bahan_utama")
    val bahan_utama: String? = null,
    @SerialName("takaran_gram")
    val takaran_gram: String? = null,
    @SerialName("takaran_sendok")
    val takaran_sendok: String? = null,
    @SerialName("bahan_pengganti")
    val bahan_pengganti: String? = null,
    @SerialName("langkah_memasak")
    val langkah_memasak: String? = null,
    @SerialName("timer_id")
    val timer_id: Long? = null,
    @SerialName("created_at")
    val created_at: String? = null,
    @SerialName("updated_at")
    val updated_at: String? = null
)

// Alias untuk kompatibilitas jika ada file lama yang masih mereferensikan DraftResepAdmin
typealias DraftResepAdmin = DraftResep
