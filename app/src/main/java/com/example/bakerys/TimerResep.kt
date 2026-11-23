package com.example.bakerys

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimerResep(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("user_id")
    val user_id: String,
    @SerialName("resep_id")
    val resep_id: Long,
    @SerialName("durasi_awal_menit")
    val durasi_awal_menit: Int,
    @SerialName("sisa_detik")
    val sisa_detik: Int,
    @SerialName("status")
    val status: String,
    @SerialName("created_at")
    val created_at: String? = null
)
