package com.example.bakerys

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BahanResep(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("resep_id")
    val resep_id: Long,
    @SerialName("nama_bahan")
    val nama_bahan: String,
    @SerialName("takaran_sendok")
    val takaran_sendok: String? = null,
    @SerialName("takaran_gram")
    val takaran_gram: String? = null,
    @SerialName("tipe_bahan")
    val tipe_bahan: String? = null
)
