package com.example.bakerys

import android.content.Context
import android.net.Uri
import android.util.Log
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.json.JSONArray
import org.json.JSONObject

object BakeryStorageManager {

    private const val PREFS_NAME = "bakery_app_database"
    private const val KEY_DISUKAI = "key_daftar_disukai"
    private const val KEY_TERSIMPAN = "key_daftar_tersimpan"
    private const val KEY_DRAFT = "key_daftar_draft"

    // Default UUID fallback jika auth session kosong agar foreign key database tetap valid
    private const val DEFAULT_FALLBACK_USER_ID = "5f56f432-b24a-434c-8ce1-b3669d34b303"

    fun dapatkanUserId(context: Context? = null): String {
        val authId = SupabaseClient.instance.auth.currentUserOrNull()?.id
        if (!authId.isNullOrEmpty()) return authId

        val savedId = context?.getSharedPreferences("BakerysPrefs", Context.MODE_PRIVATE)
            ?.getString("current_user_id", null)
        if (!savedId.isNullOrEmpty()) return savedId

        return DEFAULT_FALLBACK_USER_ID
    }

    // =========================================================================
    // 1. KELOLA DRAFT (TABEL: draft_postingan_user & LOKAL CACHE)
    // =========================================================================
    fun simpanDraftKeSupabase(
        caption: String,
        gambarUrl: String,
        context: Context? = null,
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentUserId = dapatkanUserId(context)
                val dataDraft = buildJsonObject {
                    put("user_id", currentUserId)
                    put("caption", caption)
                    put("gambar_url", gambarUrl)
                }
                SupabaseClient.instance.from("draft_postingan_user").insert(dataDraft)
                Log.d("SUPABASE_DRAFT", "Berhasil simpan ke draft_postingan_user: $caption")
                onComplete?.invoke(true)
            } catch (e: Exception) {
                Log.e("SUPABASE_DRAFT", "Gagal simpan ke draft_postingan_user: ${e.message}")
                onComplete?.invoke(false)
            }
        }
    }

    fun hapusDraftDariSupabase(caption: String, context: Context? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentUserId = dapatkanUserId(context)
                SupabaseClient.instance.from("draft_postingan_user").delete {
                    filter {
                        eq("user_id", currentUserId)
                        eq("caption", caption)
                    }
                }
                Log.d("SUPABASE_DRAFT", "Berhasil hapus draft dari Supabase: $caption")
            } catch (e: Exception) {
                Log.e("SUPABASE_DRAFT", "Gagal hapus draft dari Supabase: ${e.message}")
            }
        }
    }

    fun simpanDaftarDraft(context: Context, list: List<Recipe>) {
        try {
            val jsonArray = JSONArray()
            for (item in list) {
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("title", item.title)
                    put("description", item.description)
                    put("imageResId", item.imageResId ?: 0)
                    put("imageUri", item.imageUri?.toString() ?: "")
                }
                jsonArray.put(obj)
            }
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_DRAFT, jsonArray.toString())
                .apply()
        } catch (e: Exception) {
            Log.e("STORAGE_ERROR", "Gagal menyimpan draft lokal: ${e.message}")
        }
    }

    fun muatDaftarDraft(context: Context): MutableList<Recipe> {
        val hasil = mutableListOf<Recipe>()
        try {
            val jsonString = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_DRAFT, null) ?: return hasil

            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.optInt("id", (System.currentTimeMillis() % Int.MAX_VALUE).toInt())
                val title = obj.optString("title")
                val description = obj.optString("description")
                val imgRes = obj.optInt("imageResId", 0)
                val uriStr = obj.optString("imageUri")
                val uri = if (uriStr.isNotEmpty()) Uri.parse(uriStr) else null

                hasil.add(
                    Recipe(
                        id = id,
                        title = title,
                        description = description,
                        imageResId = if (imgRes != 0) imgRes else null,
                        imageUri = uri
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("STORAGE_ERROR", "Gagal memuat draft lokal: ${e.message}")
        }
        return hasil
    }

    // =========================================================================
    // 2. KELOLA POSTINGAN UNGGAHAN (TABEL: postingan_users & resep)
    // =========================================================================
    fun simpanPostinganKeSupabase(
        caption: String,
        gambarUrl: String,
        context: Context? = null,
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentUserId = dapatkanUserId(context)

            // 1. Masukkan ke tabel postingan_users (kolom: user_id, foto_url, caption)
            try {
                val dataPost = buildJsonObject {
                    put("user_id", currentUserId)
                    put("caption", caption)
                    put("foto_url", gambarUrl)
                }
                SupabaseClient.instance.from("postingan_users").insert(dataPost)
                Log.d("SUPABASE_POST", "Berhasil simpan ke postingan_users: $caption")
            } catch (e: Exception) {
                Log.e("SUPABASE_POST", "Gagal simpan ke postingan_users: ${e.message}")
            }

            // 2. Masukkan ke tabel resep (kolom: user_id, judul, deskripsi, gambar)
            try {
                val dataResep = buildJsonObject {
                    put("user_id", currentUserId)
                    put("judul", caption)
                    put("deskripsi", "Resep olahan lezat kreasi baker.")
                    put("gambar", gambarUrl)
                }
                SupabaseClient.instance.from("resep").insert(dataResep)
                Log.d("SUPABASE_RESEP", "Berhasil simpan ke resep: $caption")
                onComplete?.invoke(true)
            } catch (e: Exception) {
                Log.e("SUPABASE_RESEP", "Gagal simpan ke resep: ${e.message}")
                onComplete?.invoke(false)
            }
        }
    }

    // =========================================================================
    // 3. KELOLA KOMENTAR (TABEL: komentar)
    // =========================================================================
    fun simpanKomentarKeSupabase(
        resepId: Long? = 11L,
        isiKomentar: String,
        context: Context? = null,
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentUserId = dapatkanUserId(context)
                val targetResepId = if (resepId != null && resepId > 0) resepId else 11L

                val dataJson = buildJsonObject {
                    put("user_id", currentUserId)
                    put("resep_id", targetResepId)
                    put("isi_komentar", isiKomentar)
                }
                SupabaseClient.instance.from("komentar").insert(dataJson)
                Log.d("SUPABASE_KOMENTAR", "Berhasil simpan komentar: $isiKomentar")
                onComplete?.invoke(true)
            } catch (e: Exception) {
                Log.e("SUPABASE_KOMENTAR", "Gagal simpan komentar: ${e.message}")
                onComplete?.invoke(false)
            }
        }
    }

    // =========================================================================
    // 4. KELOLA SUKA / LIKE (TABEL: suka & LOKAL CACHE)
    // =========================================================================
    fun simpanSukaKeSupabase(
        context: Context? = null,
        resepId: Long? = 11L,
        isLiked: Boolean
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentUserId = dapatkanUserId(context)
                val targetResepId = if (resepId != null && resepId > 0) resepId else 11L

                if (isLiked) {
                    val dataJson = buildJsonObject {
                        put("user_id", currentUserId)
                        put("resep_id", targetResepId)
                    }
                    SupabaseClient.instance.from("suka").insert(dataJson)
                    Log.d("SUPABASE_SUKA", "Berhasil like resep #$targetResepId ke tabel suka")
                } else {
                    SupabaseClient.instance.from("suka").delete {
                        filter {
                            eq("user_id", currentUserId)
                            eq("resep_id", targetResepId)
                        }
                    }
                    Log.d("SUPABASE_SUKA", "Berhasil unlike resep #$targetResepId dari tabel suka")
                }
            } catch (e: Exception) {
                Log.e("SUPABASE_SUKA", "Gagal update tabel suka: ${e.message}")
            }
        }
    }

    fun simpanDaftarDisukai(context: Context, list: List<ResepModel>) {
        try {
            val jsonArray = JSONArray()
            for (item in list) {
                val obj = JSONObject().apply {
                    put("judul", item.judul)
                    put("deskripsi", item.deskripsi)
                    put("gambar", item.gambar ?: R.drawable.dubai)
                    put("imageUri", item.imageUri?.toString() ?: "")
                    put("isFavorite", item.isFavorite)
                }
                jsonArray.put(obj)
            }
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_DISUKAI, jsonArray.toString())
                .apply()
        } catch (e: Exception) {
            Log.e("STORAGE_ERROR", "Gagal menyimpan disukai lokal: ${e.message}")
        }
    }

    fun muatDaftarDisukai(context: Context): MutableList<ResepModel> {
        val hasil = mutableListOf<ResepModel>()
        try {
            val jsonString = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_DISUKAI, null) ?: return hasil

            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val judul = obj.optString("judul")
                val deskripsi = obj.optString("deskripsi")
                val gambar = obj.optInt("gambar", R.drawable.dubai)
                val uriStr = obj.optString("imageUri")
                val uri = if (uriStr.isNotEmpty()) Uri.parse(uriStr) else null

                hasil.add(
                    ResepModel(
                        judul = judul,
                        deskripsi = deskripsi,
                        gambar = gambar,
                        imageUri = uri,
                        isFavorite = true,
                        isDisukai = true
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("STORAGE_ERROR", "Gagal memuat disukai lokal: ${e.message}")
        }
        return hasil
    }

    // =========================================================================
    // 5. KELOLA FAVORIT / BOOKMARK (TABEL: favorit & LOKAL CACHE)
    // =========================================================================
    fun simpanFavoritKeSupabase(
        context: Context? = null,
        resepId: Long? = 11L,
        isFavorit: Boolean
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentUserId = dapatkanUserId(context)
                val targetResepId = if (resepId != null && resepId > 0) resepId else 11L

                if (isFavorit) {
                    val dataJson = buildJsonObject {
                        put("user_id", currentUserId)
                        put("resep_id", targetResepId)
                    }
                    SupabaseClient.instance.from("favorit").insert(dataJson)
                    Log.d("SUPABASE_FAVORIT", "Berhasil simpan resep #$targetResepId ke tabel favorit")
                } else {
                    SupabaseClient.instance.from("favorit").delete {
                        filter {
                            eq("user_id", currentUserId)
                            eq("resep_id", targetResepId)
                        }
                    }
                    Log.d("SUPABASE_FAVORIT", "Berhasil hapus resep #$targetResepId dari tabel favorit")
                }
            } catch (e: Exception) {
                Log.e("SUPABASE_FAVORIT", "Gagal update tabel favorit: ${e.message}")
            }
        }
    }

    fun simpanDaftarTersimpan(context: Context, list: List<Recipe>) {
        try {
            val jsonArray = JSONArray()
            for (item in list) {
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("title", item.title)
                    put("description", item.description)
                    put("imageResId", item.imageResId ?: 0)
                    put("imageUri", item.imageUri?.toString() ?: "")
                    put("isBookmarked", item.isBookmarked)
                }
                jsonArray.put(obj)
            }
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_TERSIMPAN, jsonArray.toString())
                .apply()
        } catch (e: Exception) {
            Log.e("STORAGE_ERROR", "Gagal menyimpan tersimpan lokal: ${e.message}")
        }
    }

    fun muatDaftarTersimpan(context: Context): MutableList<Recipe> {
        val hasil = mutableListOf<Recipe>()
        try {
            val jsonString = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_TERSIMPAN, null) ?: return hasil

            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.optInt("id", (System.currentTimeMillis() % Int.MAX_VALUE).toInt())
                val title = obj.optString("title")
                val description = obj.optString("description")
                val imgRes = obj.optInt("imageResId", 0)
                val uriStr = obj.optString("imageUri")
                val uri = if (uriStr.isNotEmpty()) Uri.parse(uriStr) else null

                hasil.add(
                    Recipe(
                        id = id,
                        title = title,
                        description = description,
                        imageResId = if (imgRes != 0) imgRes else null,
                        imageUri = uri,
                        isBookmarked = true
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("STORAGE_ERROR", "Gagal memuat tersimpan lokal: ${e.message}")
        }
        return hasil
    }

    // =========================================================================
    // 6. KELOLA PROFIL & PREFERENSI (TABEL: users)
    // =========================================================================
    fun simpanPreferensiKeSupabase(context: Context, kunci: String, nilai: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentUserId = dapatkanUserId(context)
                val updateJson = buildJsonObject {
                    when (kunci) {
                        "unit_preference" -> put("satuan_pengukuran", nilai)
                        "temp_preference" -> put("standar_suhu", nilai)
                        "scale_preference" -> put("skala_presisi", nilai)
                        "profile_image_uri" -> put("foto_profil", nilai)
                    }
                }
                SupabaseClient.instance.from("users").update(updateJson) {
                    filter { eq("id", currentUserId) }
                }
                Log.d("SUPABASE_USERS", "Berhasil update preferensi $kunci: $nilai ke tabel users!")
            } catch (e: Exception) {
                Log.e("SUPABASE_USERS", "Gagal update preferensi ke tabel users: ${e.message}")
            }
        }
    }
}
