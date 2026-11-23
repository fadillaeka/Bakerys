package com.example.bakerys

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotifikasiSupabase(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("user_id")
    val user_id: String,
    @SerialName("pesan")
    val pesan: String,
    @SerialName("is_read")
    val is_read: Boolean = false,
    @SerialName("created_at")
    val created_at: String? = null
)

data class NotifikasiModel(
    val pesan: String,       // Contoh: "User menyukai resep Anda."
    val waktu: String = "Baru saja",       // Contoh: "Baru saja"
    val tipe: String = "DEFAULT",        // "LIKE", "KOMENTAR", atau "FAVORIT"
    val id: Long? = null,
    val isRead: Boolean = false
)
