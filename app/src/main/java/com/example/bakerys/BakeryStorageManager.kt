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

    // =========================================================================
    // 1. KELOLA RESEP DISUKAI (LIKED RECIPES PERSISTENCE)
    // =========================================================================
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
            Log.e("STORAGE_ERROR", "Gagal menyimpan disukai: ${e.message}")
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
            Log.e("STORAGE_ERROR", "Gagal memuat disukai: ${e.message}")
        }
        return hasil
    }

    // =========================================================================
    // 2. KELOLA RESEP DRAFT (DRAFT PERSISTENCE)
    // =========================================================================
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
            Log.e("STORAGE_ERROR", "Gagal menyimpan draft: ${e.message}")
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
            Log.e("STORAGE_ERROR", "Gagal memuat draft: ${e.message}")
        }
        return hasil
    }

    // =========================================================================
    // 3. KELOLA RESEP TERSIMPAN / BOOKMARKS (BOOKMARK PERSISTENCE)
    // =========================================================================
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
            Log.e("STORAGE_ERROR", "Gagal menyimpan bookmark: ${e.message}")
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
            Log.e("STORAGE_ERROR", "Gagal memuat bookmark: ${e.message}")
        }
        return hasil
    }

    // =========================================================================
    // 4. SINKRONISASI KE SUPABASE DATABASE (POSTGREST ONLINE)
    // =========================================================================
    fun simpanResepKeSupabase(namaResep: String, deskripsi: String, onComplete: ((Boolean) -> Unit)? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentUserId = SupabaseClient.instance.auth.currentUserOrNull()?.id
                val dataJson = buildJsonObject {
                    put("nama_resep", namaResep)
                    put("deskripsi", deskripsi)
                    if (currentUserId != null) {
                        put("user_id", currentUserId)
                    }
                }
                SupabaseClient.instance.from("resep").insert(dataJson)
                Log.d("SUPABASE", "Berhasil menyimpan resep '$namaResep' ke database Supabase!")
                onComplete?.invoke(true)
            } catch (e: Exception) {
                Log.e("SUPABASE", "Gagal simpan ke Supabase: ${e.message}")
                onComplete?.invoke(false)
            }
        }
    }

    fun simpanKomentarKeSupabase(resepId: Long, isiKomentar: String, onComplete: ((Boolean) -> Unit)? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentUserId = SupabaseClient.instance.auth.currentUserOrNull()?.id ?: "guest-user"
                val dataJson = buildJsonObject {
                    put("user_id", currentUserId)
                    put("resep_id", resepId)
                    put("isi_komentar", isiKomentar)
                }
                SupabaseClient.instance.from("komentar").insert(dataJson)
                Log.d("SUPABASE", "Berhasil menyimpan komentar ke database Supabase!")
                onComplete?.invoke(true)
            } catch (e: Exception) {
                Log.e("SUPABASE", "Gagal simpan komentar ke Supabase: ${e.message}")
                onComplete?.invoke(false)
            }
        }
    }
}
