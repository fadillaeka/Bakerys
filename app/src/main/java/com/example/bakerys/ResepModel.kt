package com.example.bakerys

import android.net.Uri
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class ResepModel(
    val id: Long? = null,
    val judul: String,
    val deskripsi: String,
    val gambar: Int? = null,
    val imageUri: Uri? = null,
    val fotoUrl: String? = null,
    var isFavorite: Boolean = false,
    var isDisukai: Boolean = false
)

fun dapatkanGambarResepSesuai(judul: String, fallback: Int? = null): Int {
    if (fallback != null && fallback != 0 && fallback != R.drawable.dubai) {
        return fallback
    }
    val j = judul.lowercase()
    return when {
        j.contains("sourdough") && (j.contains("country") || j.contains("loaf")) -> R.drawable.sourdough2
        j.contains("sourdough") && j.contains("plain") -> R.drawable.plainsourdough
        j.contains("sourdough") && (j.contains("baguette") || j.contains("baguete")) -> R.drawable.baguete
        j.contains("sourdough") -> R.drawable.sourdough1
        j.contains("borodinsky") || j.contains("boron") -> R.drawable.borondisky
        j.contains("baguette") || j.contains("baguete") -> R.drawable.baguete
        j.contains("walnut") || j.contains("raisin") -> R.drawable.walnut
        j.contains("lava") -> R.drawable.lava_cakes
        j.contains("caramel") || j.contains("apple") -> R.drawable.caramel
        j.contains("rainbow") -> R.drawable.rainbow
        j.contains("brownie") || j.contains("brownies") || j.contains("fudgy") -> R.drawable.classic_choco_brownies
        j.contains("cheesecake") || j.contains("pumpkin") -> R.drawable.no_bake_pumpkin_cheesecake_bars
        j.contains("nastar") -> R.drawable.nastar
        j.contains("butter") -> R.drawable.butter
        j.contains("kastengel") || j.contains("keju") -> R.drawable.kastangel_keju
        j.contains("kucing") || j.contains("lidah") -> R.drawable.kucing
        j.contains("gajah") || j.contains("kuping") -> R.drawable.gajah
        j.contains("classic cookies") -> R.drawable.cookies
        j.contains("dubai") -> R.drawable.dubai
        j.contains("cookie") || j.contains("cookies") -> R.drawable.cookies
        j.contains("cake") -> R.drawable.rainbow
        j.contains("bread") || j.contains("roti") -> R.drawable.sourdough1
        else -> fallback ?: R.drawable.dubai
    }
}

@Serializable
data class ResepDisukai(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("user_id")
    val user_id: String,
    @SerialName("resep_id")
    val resep_id: Long,
    @SerialName("created_at")
    val created_at: String? = null
)

@Serializable
data class SukaModel(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("user_id")
    val user_id: String,
    @SerialName("resep_id")
    val resep_id: Long,
    @SerialName("created_at")
    val created_at: String? = null
)

@Serializable
data class ResepSupabase(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("user_id")
    val user_id: String,
    @SerialName("kategori_id")
    val kategori_id: Long? = null,
    @SerialName("judul")
    val judul: String,
    @SerialName("deskripsi")
    val deskripsi: String? = null,
    @SerialName("gambar")
    val gambar: String? = null,
    @SerialName("waktu_panggang")
    val waktu_panggang: Int? = null,
    @SerialName("porsi")
    val porsi: String? = null,
    @SerialName("status")
    val status: String? = "published",
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
