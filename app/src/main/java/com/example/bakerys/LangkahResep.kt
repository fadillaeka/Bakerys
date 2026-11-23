package com.example.bakerys

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LangkahResep(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("resep_id")
    val resep_id: Long,
    @SerialName("nomor_langkah")
    val nomor_langkah: Int,
    @SerialName("instruksi")
    val instruksi: String
)
