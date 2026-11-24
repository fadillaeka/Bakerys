package com.example.bakerys

import android.content.Context
import android.net.Uri
import android.util.Log
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
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
        // Coba ambil dari Supabase auth session
        val authId = try {
            SupabaseClient.instance.auth.currentUserOrNull()?.id
        } catch (e: Exception) {
            null
        }
        if (!authId.isNullOrEmpty()) return authId

        // Fallback: ambil dari SharedPreferences (disimpan saat login)
        val savedId = context?.getSharedPreferences("BakerysPrefs", Context.MODE_PRIVATE)
            ?.getString("current_user_id", null)
        if (!savedId.isNullOrEmpty()) return savedId

        return DEFAULT_FALLBACK_USER_ID
    }

    // =========================================================================
    // 1. KELOLA DRAFT (TABEL: draft_resep_admin & draft_postingan_user & LOKAL CACHE)
    // =========================================================================
    fun simpanDraftKeSupabase(
        caption: String,
        gambarUrl: String,
        context: Context? = null,
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentUserId = dapatkanUserId(context)
            var berhasil = false

            // 1. Simpan ke tabel draft_resep_admin
            try {
                val draft = DraftResepAdmin(
                    user_id = currentUserId,
                    judul = caption,
                    deskripsi = "Draft resep olahan kreasi baker.",
                    gambar = gambarUrl.ifEmpty { null },
                    status = "draft"
                )
                SupabaseClient.instance.from("draft_resep_admin").insert(draft)
                Log.d("SUPABASE_DRAFT", "Berhasil simpan ke draft_resep_admin: $caption")
                berhasil = true
            } catch (e: Exception) {
                Log.e("SUPABASE_DRAFT", "Gagal simpan ke draft_resep_admin: ${e.message}")
            }

            // 2. Simpan ke tabel draft_postingan_user (jika tabel ada)
            try {
                val dataDraft = buildJsonObject {
                    put("user_id", currentUserId)
                    put("caption", caption)
                    put("gambar_url", gambarUrl)
                }
                SupabaseClient.instance.from("draft_postingan_user").insert(dataDraft)
                Log.d("SUPABASE_DRAFT", "Berhasil simpan ke draft_postingan_user: $caption")
                berhasil = true
            } catch (e: Exception) {
                Log.e("SUPABASE_DRAFT", "Gagal simpan ke draft_postingan_user (mungkin tabel belum ada): ${e.message}")
            }

            onComplete?.invoke(berhasil)
        }
    }

    fun hapusDraftDariSupabase(caption: String, context: Context? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentUserId = dapatkanUserId(context)
                // Hapus dari draft_resep_admin
                try {
                    SupabaseClient.instance.from("draft_resep_admin").delete {
                        filter {
                            eq("user_id", currentUserId)
                            eq("judul", caption)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SUPABASE_DRAFT", "Gagal hapus dari draft_resep_admin: ${e.message}")
                }

                // Hapus dari draft_postingan_user
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

    suspend fun muatDraftResepDariSupabase(context: Context? = null): List<DraftResep> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUserId = dapatkanUserId(context)
                // Coba ambil dari draft_resep terlebih dahulu
                try {
                    val list = SupabaseClient.instance.from("draft_resep")
                        .select {
                            filter { eq("user_id", currentUserId) }
                        }
                        .decodeList<DraftResep>()
                    return@withContext list.sortedByDescending { it.created_at }
                } catch (e1: Exception) {
                    // Fallback ke draft_resep_admin jika tabel di Supabase belum dimigrasi
                    val list = SupabaseClient.instance.from("draft_resep_admin")
                        .select {
                            filter { eq("user_id", currentUserId) }
                        }
                        .decodeList<DraftResep>()
                    return@withContext list.sortedByDescending { it.created_at }
                }
            } catch (e: Exception) {
                Log.e("SUPABASE_DRAFT", "Gagal muat draft resep: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun muatDraftAdminDariSupabase(context: Context? = null): List<DraftResepAdmin> = muatDraftResepDariSupabase(context)

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
        onComplete: ((Boolean, String?) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentUserId = dapatkanUserId(context)

            // Masukkan ke tabel postingan_users (khusus feed Jelajah)
            try {
                val dataPost = buildJsonObject {
                    put("user_id", currentUserId)
                    put("caption", caption)
                    put("foto_url", gambarUrl)
                }
                SupabaseClient.instance.from("postingan_users").insert(dataPost)
                Log.d("SUPABASE_POST", "Berhasil simpan ke postingan_users: $caption")
                onComplete?.invoke(true, null)
            } catch (e: Exception) {
                Log.e("SUPABASE_POST", "Gagal simpan ke postingan_users: ${e.message}", e)
                onComplete?.invoke(false, e.message)
            }
        }
    }

    // =========================================================================
    // 3. KELOLA KOMENTAR (TABEL: komentar)
    // =========================================================================
    suspend fun dapatkanResepIdValid(targetId: Long?): Long {
        return withContext(Dispatchers.IO) {
            // 1. Jika targetId diberikan dan > 0, cek apakah benar-benar ada di tabel resep
            if (targetId != null && targetId > 0) {
                try {
                    val rows = SupabaseClient.instance.from("resep").select {
                        filter { eq("id", targetId) }
                        limit(1)
                    }.decodeList<JsonObject>()
                    if (rows.isNotEmpty()) {
                        return@withContext targetId
                    }
                } catch (e: Exception) {
                    Log.w("SUPABASE_KOMENTAR", "Cek resep #$targetId gagal: ${e.message}")
                }
            }

            // 2. Query ID resep yang benar-benar ada di tabel resep Supabase
            try {
                val list = SupabaseClient.instance.from("resep").select {
                    limit(1)
                }.decodeList<JsonObject>()
                val firstId = list.firstOrNull()?.get("id")?.jsonPrimitive?.content?.toLongOrNull()
                if (firstId != null && firstId > 0) {
                    Log.d("SUPABASE_KOMENTAR", "Menggunakan resepId valid dari database: #$firstId")
                    return@withContext firstId
                }
            } catch (e: Exception) {
                Log.e("SUPABASE_KOMENTAR", "Gagal ambil resepId pertama: ${e.message}")
            }

            // Fallback ID default yang terbukti ada di tabel resep
            21L
        }
    }

    suspend fun muatKomentarDariSupabase(resepId: Long): List<CommentItem> {
        return withContext(Dispatchers.IO) {
            val hasil = mutableListOf<CommentItem>()
            try {
                val validResepId = dapatkanResepIdValid(resepId)
                val listKomen = SupabaseClient.instance.from("komentar").select {
                    filter { eq("resep_id", validResepId) }
                }.decodeList<JsonObject>()

                for ((idx, k) in listKomen.withIndex()) {
                    val isi = k["isi_komentar"]?.jsonPrimitive?.content ?: continue
                    val userId = k["user_id"]?.jsonPrimitive?.content
                    var namaUser = "Baker #${idx + 1}"

                    if (!userId.isNullOrEmpty()) {
                        try {
                            val userRow = SupabaseClient.instance.from("users").select {
                                filter { eq("id", userId) }
                            }.decodeList<JsonObject>().firstOrNull()

                            val uName = userRow?.get("username")?.jsonPrimitive?.content
                            val nLengkap = userRow?.get("nama_lengkap")?.jsonPrimitive?.content
                            if (!uName.isNullOrEmpty()) {
                                namaUser = uName
                            } else if (!nLengkap.isNullOrEmpty()) {
                                namaUser = nLengkap
                            }
                        } catch (eUser: Exception) {
                            // Fallback nama default
                        }
                    }

                    hasil.add(
                        CommentItem(
                            username = namaUser,
                            text = isi
                        )
                    )
                }
                Log.d("SUPABASE_KOMENTAR", "Berhasil muat ${hasil.size} komentar untuk resep #$validResepId")
            } catch (e: Exception) {
                Log.e("SUPABASE_KOMENTAR", "Gagal muat komentar #$resepId: ${e.message}")
            }
            hasil
        }
    }

    suspend fun simpanKomentarKeSupabaseSuspend(
        resepId: Long?,
        isiKomentar: String,
        context: Context? = null
    ): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUserId = dapatkanUserId(context)
                val targetResepId = dapatkanResepIdValid(resepId)

                val dataJson = buildJsonObject {
                    put("user_id", currentUserId)
                    put("resep_id", targetResepId)
                    put("isi_komentar", isiKomentar)
                }
                SupabaseClient.instance.from("komentar").insert(dataJson)
                Log.d("SUPABASE_KOMENTAR", "Berhasil simpan komentar pada resep #$targetResepId: $isiKomentar")
                Result.success(true)
            } catch (e: Exception) {
                Log.e("SUPABASE_KOMENTAR", "Gagal simpan komentar: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    fun simpanKomentarKeSupabase(
        resepId: Long? = null,
        isiKomentar: String,
        context: Context? = null,
        onComplete: ((Boolean, String?) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = simpanKomentarKeSupabaseSuspend(resepId, isiKomentar, context)
            if (result.isSuccess) {
                onComplete?.invoke(true, null)
            } else {
                val msg = result.exceptionOrNull()?.message ?: "Gagal menyimpan komentar ke database"
                onComplete?.invoke(false, msg)
            }
        }
    }

    // =========================================================================
    // 4. KELOLA SUKA / LIKE POSTINGAN (TABEL: suka)
    // =========================================================================
    fun simpanSukaKeSupabase(
        context: Context? = null,
        resepId: Long? = 11L,
        isLiked: Boolean,
        onComplete: ((Int) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val targetResepId = if (resepId != null && resepId > 0) resepId else 11L
            try {
                val currentUserId = dapatkanUserId(context)

                if (isLiked) {
                    val dataJson = buildJsonObject {
                        put("user_id", currentUserId)
                        put("resep_id", targetResepId)
                    }
                    SupabaseClient.instance.from("suka").insert(dataJson)
                    Log.d("SUPABASE_SUKA", "Berhasil like resep/postingan #$targetResepId ke tabel suka")
                } else {
                    SupabaseClient.instance.from("suka").delete {
                        filter {
                            eq("user_id", currentUserId)
                            eq("resep_id", targetResepId)
                        }
                    }
                    Log.d("SUPABASE_SUKA", "Berhasil unlike resep/postingan #$targetResepId dari tabel suka")
                }

                val count = ambilJumlahSuka(targetResepId)
                onComplete?.invoke(count)
            } catch (e: Exception) {
                Log.e("SUPABASE_SUKA", "Gagal update tabel suka: ${e.message}")
                val fallbackCount = ambilJumlahSuka(targetResepId)
                onComplete?.invoke(fallbackCount)
            }
        }
    }

    suspend fun ambilJumlahSuka(resepId: Long): Int {
        return withContext(Dispatchers.IO) {
            try {
                val list = SupabaseClient.instance.from("suka").select {
                    filter { eq("resep_id", resepId) }
                }.decodeList<JsonObject>()
                list.size
            } catch (e: Exception) {
                Log.e("SUPABASE_SUKA", "Gagal ambil jumlah suka #$resepId: ${e.message}")
                0
            }
        }
    }

    suspend fun cekUserMenyukaiPostingan(context: Context?, resepId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val currentUserId = dapatkanUserId(context)
                val list = SupabaseClient.instance.from("suka").select {
                    filter {
                        eq("user_id", currentUserId)
                        eq("resep_id", resepId)
                    }
                }.decodeList<JsonObject>()
                list.isNotEmpty()
            } catch (e: Exception) {
                false
            }
        }
    }

    // =========================================================================
    // 4B. KELOLA RESEP DISUKAI (TABEL: resep_disukai & LOKAL CACHE)
    // =========================================================================
    fun simpanResepDisukaiKeSupabase(
        context: Context? = null,
        resepId: Long? = 11L,
        isDisukai: Boolean,
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentUserId = dapatkanUserId(context)
                val targetResepId = if (resepId != null && resepId > 0) resepId else 11L

                if (isDisukai) {
                    val dataJson = buildJsonObject {
                        put("user_id", currentUserId)
                        put("resep_id", targetResepId)
                    }
                    SupabaseClient.instance.from("resep_disukai").insert(dataJson)
                    Log.d("SUPABASE_DISUKAI", "Berhasil simpan resep #$targetResepId ke tabel resep_disukai")
                } else {
                    SupabaseClient.instance.from("resep_disukai").delete {
                        filter {
                            eq("user_id", currentUserId)
                            eq("resep_id", targetResepId)
                        }
                    }
                    Log.d("SUPABASE_DISUKAI", "Berhasil hapus resep #$targetResepId dari tabel resep_disukai")
                }
                onComplete?.invoke(true)
            } catch (e: Exception) {
                Log.e("SUPABASE_DISUKAI", "Gagal update tabel resep_disukai: ${e.message}")
                onComplete?.invoke(false)
            }
        }
    }

    suspend fun muatResepDisukaiDariSupabase(context: Context? = null): List<ResepModel> {
        return withContext(Dispatchers.IO) {
            val hasil = mutableListOf<ResepModel>()
            try {
                val currentUserId = dapatkanUserId(context)
                val listDisukai = SupabaseClient.instance.from("resep_disukai").select {
                    filter { eq("user_id", currentUserId) }
                }.decodeList<ResepDisukai>()

                for (item in listDisukai) {
                    try {
                        val resepRows = SupabaseClient.instance.from("resep").select {
                            filter { eq("id", item.resep_id) }
                        }.decodeList<JsonObject>()

                        if (resepRows.isNotEmpty()) {
                            val r = resepRows.first()
                            val judul = r["judul"]?.jsonPrimitive?.content ?: "Resep #${item.resep_id}"
                            val deskripsi = r["deskripsi"]?.jsonPrimitive?.content ?: "Resep olahan lezat kreasi baker."
                            val foto = r["gambar"]?.jsonPrimitive?.content
                            val uri = if (!foto.isNullOrEmpty()) Uri.parse(foto) else null

                            hasil.add(
                                ResepModel(
                                    id = item.resep_id,
                                    judul = judul,
                                    deskripsi = deskripsi,
                                    gambar = if (uri == null) R.drawable.dubai else null,
                                    imageUri = uri,
                                    fotoUrl = foto,
                                    isFavorite = true,
                                    isDisukai = true
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("SUPABASE_DISUKAI", "Gagal muat resep #${item.resep_id}: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("SUPABASE_DISUKAI", "Gagal muat tabel resep_disukai: ${e.message}")
            }
            hasil
        }
    }

    suspend fun ambilDaftarIdResepDisukai(context: Context? = null): Set<Long> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUserId = dapatkanUserId(context)
                val list = SupabaseClient.instance.from("resep_disukai").select {
                    filter { eq("user_id", currentUserId) }
                }.decodeList<ResepDisukai>()
                list.map { it.resep_id }.toSet()
            } catch (e: Exception) {
                emptySet()
            }
        }
    }

    suspend fun cariAtauBuatResep(
        judul: String,
        deskripsi: String,
        gambarUrl: String? = null,
        context: Context? = null
    ): Long {
        return withContext(Dispatchers.IO) {
            try {
                val existing = SupabaseClient.instance.from("resep").select {
                    filter { eq("judul", judul) }
                }.decodeList<JsonObject>()

                if (existing.isNotEmpty()) {
                    val idVal = existing.first()["id"]?.jsonPrimitive?.content?.toLongOrNull()
                    if (idVal != null && idVal > 0) return@withContext idVal
                }

                val currentUserId = dapatkanUserId(context)
                val dataResep = buildJsonObject {
                    put("user_id", currentUserId)
                    put("judul", judul)
                    put("deskripsi", deskripsi)
                    if (!gambarUrl.isNullOrEmpty()) put("gambar", gambarUrl)
                    put("status", "published")
                }
                val inserted = SupabaseClient.instance.from("resep").insert(dataResep) {
                    select()
                }.decodeList<JsonObject>()

                inserted.firstOrNull()?.get("id")?.jsonPrimitive?.content?.toLongOrNull() ?: 11L
            } catch (e: Exception) {
                Log.e("SUPABASE_RESEP", "Gagal cari/buat resep: ${e.message}")
                11L
            }
        }
    }

    fun simpanDaftarDisukai(context: Context, list: List<ResepModel>) {
        try {
            val jsonArray = JSONArray()
            for (item in list) {
                val obj = JSONObject().apply {
                    put("id", item.id ?: 0L)
                    put("judul", item.judul)
                    put("deskripsi", item.deskripsi)
                    put("gambar", item.gambar ?: R.drawable.dubai)
                    put("imageUri", item.imageUri?.toString() ?: "")
                    put("fotoUrl", item.fotoUrl ?: "")
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
                val idVal = obj.optLong("id", 0L)
                val judul = obj.optString("judul")
                val deskripsi = obj.optString("deskripsi")
                val gambar = obj.optInt("gambar", R.drawable.dubai)
                val uriStr = obj.optString("imageUri")
                val fotoUrlStr = obj.optString("fotoUrl")
                val uri = if (uriStr.isNotEmpty()) Uri.parse(uriStr) else if (fotoUrlStr.isNotEmpty()) Uri.parse(fotoUrlStr) else null

                hasil.add(
                    ResepModel(
                        id = if (idVal > 0) idVal else null,
                        judul = judul,
                        deskripsi = deskripsi,
                        gambar = gambar,
                        imageUri = uri,
                        fotoUrl = fotoUrlStr.ifEmpty { null },
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

    // =========================================================================
    // 7. KELOLA KATEGORI (TABEL: kategori)
    // =========================================================================
    suspend fun ambilDaftarKategori(): List<Kategori> {
        return withContext(Dispatchers.IO) {
            try {
                SupabaseClient.instance.from("kategori")
                    .select()
                    .decodeList<Kategori>()
            } catch (e: Exception) {
                Log.e("SUPABASE_KATEGORI", "Gagal memuat kategori: ${e.message}")
                emptyList()
            }
        }
    }

    // =========================================================================
    // 8. KELOLA BAHAN RESEP (TABEL: bahan_resep)
    // =========================================================================
    suspend fun ambilBahanResep(resepId: Long): List<BahanResep> {
        return withContext(Dispatchers.IO) {
            try {
                SupabaseClient.instance.from("bahan_resep").select {
                    filter { eq("resep_id", resepId) }
                }.decodeList<BahanResep>()
            } catch (e: Exception) {
                Log.e("SUPABASE_BAHAN", "Gagal memuat bahan_resep #$resepId: ${e.message}")
                emptyList()
            }
        }
    }

    fun simpanBahanResep(bahan: BahanResep, onComplete: ((Boolean) -> Unit)? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SupabaseClient.instance.from("bahan_resep").insert(bahan)
                Log.d("SUPABASE_BAHAN", "Berhasil simpan bahan: ${bahan.nama_bahan}")
                onComplete?.invoke(true)
            } catch (e: Exception) {
                Log.e("SUPABASE_BAHAN", "Gagal simpan bahan: ${e.message}")
                onComplete?.invoke(false)
            }
        }
    }

    // =========================================================================
    // 9. KELOLA LANGKAH RESEP (TABEL: langkah_resep)
    // =========================================================================
    suspend fun ambilLangkahResep(resepId: Long): List<LangkahResep> {
        return withContext(Dispatchers.IO) {
            try {
                SupabaseClient.instance.from("langkah_resep").select {
                    filter { eq("resep_id", resepId) }
                }.decodeList<LangkahResep>().sortedBy { it.nomor_langkah }
            } catch (e: Exception) {
                Log.e("SUPABASE_LANGKAH", "Gagal memuat langkah_resep #$resepId: ${e.message}")
                emptyList()
            }
        }
    }

    fun simpanLangkahResep(langkah: LangkahResep, onComplete: ((Boolean) -> Unit)? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SupabaseClient.instance.from("langkah_resep").insert(langkah)
                Log.d("SUPABASE_LANGKAH", "Berhasil simpan langkah ${langkah.nomor_langkah}")
                onComplete?.invoke(true)
            } catch (e: Exception) {
                Log.e("SUPABASE_LANGKAH", "Gagal simpan langkah: ${e.message}")
                onComplete?.invoke(false)
            }
        }
    }

    // =========================================================================
    // 10. KELOLA TIMER RESEP (TABEL: timer_resep)
    // =========================================================================
    fun simpanAtauUpdateTimerKeSupabase(
        resepId: Long,
        durasiAwalMenit: Int,
        sisaDetik: Int,
        status: String,
        context: Context? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentUserId = dapatkanUserId(context)
                val dataTimer = buildJsonObject {
                    put("user_id", currentUserId)
                    put("resep_id", resepId)
                    put("durasi_awal_menit", durasiAwalMenit)
                    put("sisa_detik", sisaDetik)
                    put("status", status)
                }
                SupabaseClient.instance.from("timer_resep").insert(dataTimer)
                Log.d("SUPABASE_TIMER", "Berhasil simpan status timer resep #$resepId: $status ($sisaDetik s)")
            } catch (e: Exception) {
                Log.e("SUPABASE_TIMER", "Gagal simpan timer: ${e.message}")
            }
        }
    }

    suspend fun ambilStatusTimerDariSupabase(resepId: Long, context: Context? = null): TimerResep? {
        return withContext(Dispatchers.IO) {
            try {
                val currentUserId = dapatkanUserId(context)
                SupabaseClient.instance.from("timer_resep").select {
                    filter {
                        eq("user_id", currentUserId)
                        eq("resep_id", resepId)
                    }
                }.decodeList<TimerResep>().lastOrNull()
            } catch (e: Exception) {
                Log.e("SUPABASE_TIMER", "Gagal ambil timer: ${e.message}")
                null
            }
        }
    }

    // =========================================================================
    // 11. KELOLA NOTIFIKASI (TABEL: notifikasi)
    // =========================================================================
    fun simpanNotifikasiKeSupabase(pesan: String, context: Context? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentUserId = dapatkanUserId(context)
                val dataNotif = buildJsonObject {
                    put("user_id", currentUserId)
                    put("pesan", pesan)
                    put("is_read", false)
                }
                SupabaseClient.instance.from("notifikasi").insert(dataNotif)
                Log.d("SUPABASE_NOTIF", "Berhasil simpan notifikasi: $pesan")
            } catch (e: Exception) {
                Log.e("SUPABASE_NOTIF", "Gagal simpan notifikasi: ${e.message}")
            }
        }
    }

    suspend fun ambilNotifikasiDariSupabase(context: Context? = null): List<NotifikasiSupabase> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUserId = dapatkanUserId(context)
                SupabaseClient.instance.from("notifikasi").select {
                    filter { eq("user_id", currentUserId) }
                }.decodeList<NotifikasiSupabase>()
            } catch (e: Exception) {
                Log.e("SUPABASE_NOTIF", "Gagal memuat notifikasi: ${e.message}")
                emptyList()
            }
        }
    }

    // =========================================================================
    // 12. UPLOAD & PUBLIKASI RESEP (TABEL: resep, bahan_resep, langkah_resep, draft_resep)
    // =========================================================================
    suspend fun publikasikanResep(
        resep: ResepSupabase,
        listBahan: List<BahanResep>,
        listLangkah: List<LangkahResep>
    ): Long? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Insert ke tabel resep
                val insertedResep = SupabaseClient.instance.from("resep").insert(resep) {
                    select()
                }.decodeList<JsonObject>()

                val newResepId = insertedResep.firstOrNull()?.get("id")?.jsonPrimitive?.content?.toLongOrNull()
                if (newResepId != null && newResepId > 0) {
                    // 2. Insert bahan_resep
                    for (bahan in listBahan) {
                        try {
                            val bahanData = bahan.copy(resep_id = newResepId)
                            SupabaseClient.instance.from("bahan_resep").insert(bahanData)
                        } catch (e: Exception) {
                            Log.e("SUPABASE_RESEP", "Gagal insert bahan: ${e.message}")
                        }
                    }

                    // 3. Insert langkah_resep
                    for (langkah in listLangkah) {
                        try {
                            val langkahData = langkah.copy(resep_id = newResepId)
                            SupabaseClient.instance.from("langkah_resep").insert(langkahData)
                        } catch (e: Exception) {
                            Log.e("SUPABASE_RESEP", "Gagal insert langkah: ${e.message}")
                        }
                    }
                    Log.d("SUPABASE_RESEP", "Berhasil publikasikan resep #$newResepId beserta bahan dan langkah")
                    return@withContext newResepId
                }
                null
            } catch (e: Exception) {
                Log.e("SUPABASE_RESEP", "Gagal publikasikan resep: ${e.message}")
                null
            }
        }
    }

    // Alias untuk kompatibilitas
    suspend fun publikasikanResepAdmin(
        resep: ResepSupabase,
        listBahan: List<BahanResep>,
        listLangkah: List<LangkahResep>
    ): Long? = publikasikanResep(resep, listBahan, listLangkah)

    suspend fun simpanDraftResep(draft: DraftResep): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Prioritaskan tabel draft_resep
                try {
                    SupabaseClient.instance.from("draft_resep").insert(draft)
                    Log.d("SUPABASE_DRAFT", "Berhasil simpan ke draft_resep: ${draft.judul}")
                    return@withContext true
                } catch (e1: Exception) {
                    // Fallback jika database Supabase belum selesai dimigrasi dari draft_resep_admin
                    SupabaseClient.instance.from("draft_resep_admin").insert(draft)
                    Log.d("SUPABASE_DRAFT", "Berhasil simpan ke draft_resep_admin (fallback): ${draft.judul}")
                    return@withContext true
                }
            } catch (e: Exception) {
                Log.e("SUPABASE_DRAFT", "Gagal simpan draft resep: ${e.message}")
                false
            }
        }
    }

    suspend fun simpanDraftResepAdmin(draft: DraftResepAdmin): Boolean = simpanDraftResep(draft)

    suspend fun hapusDraftResep(draftId: String?, draftJudul: String? = null): Boolean {
        return withContext(Dispatchers.IO) {
            var anySuccess = false
            // 1. Coba hapus dari draft_resep
            try {
                if (!draftId.isNullOrEmpty()) {
                    SupabaseClient.instance.from("draft_resep").delete {
                        filter { eq("id", draftId) }
                    }
                    anySuccess = true
                }
            } catch (e: Exception) {
                Log.d("SUPABASE_DRAFT", "Hapus draft_resep id: ${e.message}")
            }
            try {
                if (!draftJudul.isNullOrEmpty()) {
                    SupabaseClient.instance.from("draft_resep").delete {
                        filter { eq("judul", draftJudul) }
                    }
                    anySuccess = true
                }
            } catch (e: Exception) {
                Log.d("SUPABASE_DRAFT", "Hapus draft_resep judul: ${e.message}")
            }

            // 2. Coba hapus dari draft_resep_admin (fallback / tabel utama admin)
            try {
                if (!draftId.isNullOrEmpty()) {
                    SupabaseClient.instance.from("draft_resep_admin").delete {
                        filter { eq("id", draftId) }
                    }
                    anySuccess = true
                }
            } catch (e: Exception) {
                Log.d("SUPABASE_DRAFT", "Hapus draft_resep_admin id: ${e.message}")
            }
            try {
                if (!draftJudul.isNullOrEmpty()) {
                    SupabaseClient.instance.from("draft_resep_admin").delete {
                        filter { eq("judul", draftJudul) }
                    }
                    anySuccess = true
                }
            } catch (e: Exception) {
                Log.d("SUPABASE_DRAFT", "Hapus draft_resep_admin judul: ${e.message}")
            }

            Log.d("SUPABASE_DRAFT", "Selesai proses hapus draft: id=$draftId, judul=$draftJudul")
            anySuccess
        }
    }

    suspend fun hapusDraftResepAdmin(draftId: String?, draftJudul: String? = null): Boolean = hapusDraftResep(draftId, draftJudul)

    // =========================================================================
    // 13. SINKRONISASI RESEP UTAMA UNTUK BERANDA (TABEL: resep)
    // =========================================================================
    suspend fun ambilResepBerandaDariSupabase(): List<ResepModel> {
        return withContext(Dispatchers.IO) {
            val hasil = mutableListOf<ResepModel>()
            try {
                val listData = SupabaseClient.instance.from("resep")
                    .select {
                        order("id", Order.DESCENDING)
                    }
                    .decodeList<JsonObject>()

                for (item in listData) {
                    val status = item["status"]?.jsonPrimitive?.content
                    if (status != null && (status.equals("draft", ignoreCase = true) || status.equals("hidden", ignoreCase = true))) continue

                    val idVal = item["id"]?.jsonPrimitive?.content?.toLongOrNull() ?: continue
                    val judul = item["judul"]?.jsonPrimitive?.content ?: continue
                    val deskripsi = item["deskripsi"]?.jsonPrimitive?.content ?: "Resep olahan lezat kreasi baker."
                    val waktuPanggang = item["waktu_panggang"]?.jsonPrimitive?.content
                    val porsi = item["porsi"]?.jsonPrimitive?.content
                    val foto = item["gambar"]?.jsonPrimitive?.content
                        ?: item["foto_url"]?.jsonPrimitive?.content
                        ?: item["gambar_url"]?.jsonPrimitive?.content
                    val uri = if (!foto.isNullOrEmpty()) {
                        try { Uri.parse(foto) } catch (e: Exception) { null }
                    } else null

                    val descFormatted = if (!waktuPanggang.isNullOrEmpty() && waktuPanggang != "0" && waktuPanggang != "null") {
                        val porsiStr = if (!porsi.isNullOrEmpty() && porsi != "null") " • $porsi" else ""
                        "⏱️ ${waktuPanggang}m$porsiStr • $deskripsi"
                    } else {
                        deskripsi
                    }

                    hasil.add(
                        ResepModel(
                            id = idVal,
                            judul = judul,
                            deskripsi = descFormatted,
                            gambar = if (uri == null) dapatkanGambarResepSesuai(judul) else null,
                            imageUri = uri,
                            fotoUrl = foto
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("SUPABASE_BERANDA", "Gagal muat resep beranda dari Supabase: ${e.message}")
            }
            hasil
        }
    }

    // =========================================================================
    // 14. PANEL MODERATOR: MODERASI KONTEN (resep, postingan_users, komentar)
    // =========================================================================
    suspend fun ambilSemuaKontenModerasi(): List<KontenModerasi> {
        return withContext(Dispatchers.IO) {
            val list = mutableListOf<KontenModerasi>()

            // 1. Ambil Resep
            try {
                val resepRows = SupabaseClient.instance.from("resep").select().decodeList<JsonObject>()
                for (r in resepRows) {
                    val idVal = r["id"]?.jsonPrimitive?.content?.toLongOrNull()
                    val judul = r["judul"]?.jsonPrimitive?.content ?: "Resep #${idVal ?: 0}"
                    val deskripsi = r["deskripsi"]?.jsonPrimitive?.content ?: ""
                    val statusRaw = r["status"]?.jsonPrimitive?.contentOrNull
                    val status = if (statusRaw.isNullOrEmpty() || statusRaw == "null") "published" else statusRaw
                    val userId = r["user_id"]?.jsonPrimitive?.content
                    val createdAt = r["created_at"]?.jsonPrimitive?.content

                    list.add(
                        KontenModerasi(
                            id = (idVal ?: 0L).toString(),
                            rawId = idVal,
                            tipe = "RESEP",
                            judul = judul,
                            deskripsi = deskripsi,
                            status = status,
                            userId = userId,
                            createdAt = createdAt
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("SUPABASE_MODERASI", "Gagal ambil resep untuk moderasi: ${e.message}")
            }

            // 2. Ambil Postingan Users
            try {
                val postRows = SupabaseClient.instance.from("postingan_users").select().decodeList<JsonObject>()
                for (p in postRows) {
                    val idVal = p["id"]?.jsonPrimitive?.content?.toLongOrNull()
                    val caption = p["caption"]?.jsonPrimitive?.content ?: "(Postingan tanpa teks)"
                    val userId = p["user_id"]?.jsonPrimitive?.content
                    val createdAt = p["created_at"]?.jsonPrimitive?.content

                    list.add(
                        KontenModerasi(
                            id = (idVal ?: 0L).toString(),
                            rawId = idVal,
                            tipe = "POSTINGAN",
                            judul = caption,
                            deskripsi = "Postingan kreasi pengguna",
                            status = "aktif",
                            userId = userId,
                            createdAt = createdAt
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("SUPABASE_MODERASI", "Gagal ambil postingan untuk moderasi: ${e.message}")
            }

            // 3. Ambil Komentar
            try {
                val komenRows = SupabaseClient.instance.from("komentar").select().decodeList<JsonObject>()
                for (k in komenRows) {
                    val idVal = k["id"]?.jsonPrimitive?.content?.toLongOrNull()
                    val resepId = k["resep_id"]?.jsonPrimitive?.content ?: "?"
                    val isi = k["isi_komentar"]?.jsonPrimitive?.content ?: ""
                    val userId = k["user_id"]?.jsonPrimitive?.content
                    val createdAt = k["created_at"]?.jsonPrimitive?.content

                    list.add(
                        KontenModerasi(
                            id = (idVal ?: 0L).toString(),
                            rawId = idVal,
                            tipe = "KOMENTAR",
                            judul = "Komentar pada Resep #$resepId",
                            deskripsi = isi,
                            status = "aktif",
                            userId = userId,
                            createdAt = createdAt
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("SUPABASE_MODERASI", "Gagal ambil komentar untuk moderasi: ${e.message}")
            }

            list.reversed()
        }
    }

    suspend fun sembunyikanResepModerasi(resepId: Long, sembunyikan: Boolean): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val newStatus = if (sembunyikan) "hidden" else "published"
                val updateData = buildJsonObject {
                    put("status", newStatus)
                }
                SupabaseClient.instance.from("resep").update(updateData) {
                    filter { eq("id", resepId) }
                }
                Log.d("SUPABASE_MODERASI", "Resep #$resepId berhasil diubah status menjadi $newStatus")
                true
            } catch (e: Exception) {
                Log.e("SUPABASE_MODERASI", "Gagal ubah status resep #$resepId: ${e.message}")
                false
            }
        }
    }

    suspend fun hapusKontenModerasi(tipe: String, rawId: Long?): Boolean {
        return withContext(Dispatchers.IO) {
            val targetId = rawId ?: return@withContext false
            try {
                when (tipe) {
                    "RESEP" -> {
                        // Hapus bahan dan langkah terkait terlebih dahulu
                        try {
                            SupabaseClient.instance.from("bahan_resep").delete {
                                filter { eq("resep_id", targetId) }
                            }
                        } catch (e: Exception) { /* ignore */ }
                        try {
                            SupabaseClient.instance.from("langkah_resep").delete {
                                filter { eq("resep_id", targetId) }
                            }
                        } catch (e: Exception) { /* ignore */ }

                        SupabaseClient.instance.from("resep").delete {
                            filter { eq("id", targetId) }
                        }
                    }
                    "POSTINGAN" -> {
                        try {
                            SupabaseClient.instance.from("suka").delete {
                                filter { eq("resep_id", targetId) }
                            }
                        } catch (e: Exception) { /* ignore */ }
                        try {
                            SupabaseClient.instance.from("komentar").delete {
                                filter { eq("resep_id", targetId) }
                            }
                        } catch (e: Exception) { /* ignore */ }

                        SupabaseClient.instance.from("postingan_users").delete {
                            filter { eq("id", targetId) }
                        }
                    }
                    "KOMENTAR" -> {
                        SupabaseClient.instance.from("komentar").delete {
                            filter { eq("id", targetId) }
                        }
                    }
                }
                Log.d("SUPABASE_MODERASI", "Berhasil hapus konten $tipe #$targetId")
                true
            } catch (e: Exception) {
                Log.e("SUPABASE_MODERASI", "Gagal hapus konten $tipe #$targetId: ${e.message}")
                false
            }
        }
    }

    // =========================================================================
    // 15. PANEL MODERATOR: MANAJEMEN PENGGUNA (tabel: users)
    // =========================================================================
    suspend fun ambilSemuaUserModerasi(): List<UserModerasi> {
        return withContext(Dispatchers.IO) {
            val list = mutableListOf<UserModerasi>()
            try {
                val userRows = SupabaseClient.instance.from("users").select().decodeList<JsonObject>()
                for (u in userRows) {
                    val id = u["id"]?.jsonPrimitive?.content ?: continue
                    val username = u["username"]?.jsonPrimitive?.content ?: ""
                    val namaLengkap = u["nama_lengkap"]?.jsonPrimitive?.content
                    val email = u["email"]?.jsonPrimitive?.content ?: ""
                    val role = u["role"]?.jsonPrimitive?.content ?: "user"
                    val foto = u["foto_profil"]?.jsonPrimitive?.content

                    list.add(
                        UserModerasi(
                            id = id,
                            username = username,
                            namaLengkap = namaLengkap,
                            email = email,
                            role = role,
                            fotoProfil = foto
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("SUPABASE_MODERASI", "Gagal ambil daftar user: ${e.message}")
            }
            list
        }
    }

    suspend fun ubahStatusBlokirUser(userId: String, blokir: Boolean): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val newRole = if (blokir) "blocked" else "user"
                val updateData = buildJsonObject {
                    put("role", newRole)
                }
                SupabaseClient.instance.from("users").update(updateData) {
                    filter { eq("id", userId) }
                }
                Log.d("SUPABASE_MODERASI", "Status role user $userId diubah menjadi $newRole")
                true
            } catch (e: Exception) {
                Log.e("SUPABASE_MODERASI", "Gagal ubah status user $userId: ${e.message}")
                false
            }
        }
    }
}
