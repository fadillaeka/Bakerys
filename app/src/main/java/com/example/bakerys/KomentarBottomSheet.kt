package com.example.bakerys

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class KomentarBottomSheet(
    private val commentsList: MutableList<CommentItem>,
    private val resepId: Long = 11L
) : BottomSheetDialogFragment() {

    private lateinit var adapter: KomentarAdapter

    // Palet variasi warna avatar
    private val avatarColors = listOf(
        Color.parseColor("#B8383B"),
        Color.parseColor("#D87070"),
        Color.parseColor("#E57373"),
        Color.parseColor("#C62828"),
        Color.parseColor("#6D1B1B"),
        Color.parseColor("#E53935")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_komentar, container, false)

        val btnClose = view.findViewById<ImageView>(R.id.btnCloseComment)
        val etComment = view.findViewById<EditText>(R.id.etAddComment)
        val btnSend = view.findViewById<ImageView>(R.id.btnSendComment)
        val rvComments = view.findViewById<RecyclerView>(R.id.rvComments)

        adapter = KomentarAdapter(commentsList)
        rvComments?.layoutManager = LinearLayoutManager(context)
        rvComments?.adapter = adapter

        // Muat komentar dari Supabase saat bottom sheet dibuka
        lifecycleScope.launch {
            try {
                val listDariDb = BakeryStorageManager.muatKomentarDariSupabase(resepId)
                commentsList.clear()
                commentsList.addAll(listDariDb)
                adapter.notifyDataSetChanged()
                if (commentsList.isNotEmpty()) {
                    rvComments?.smoothScrollToPosition(commentsList.size - 1)
                }
            } catch (e: Exception) {
                android.util.Log.e("KOMENTAR_SHEET", "Gagal muat komentar: ${e.message}")
            }
        }

        btnClose?.setOnClickListener {
            dismiss()
        }

        // Daftar kata-kata terlarang / tidak pantas (Bad Word Filter)
        val kataKasarList = listOf(
            "anjing", "babi", "bangsat", "kontol", "memek", "jembut", "pantek", "cuki", "judol", "burit", "anjir", "anjay", "judi online", "bapuki", "pemai",
            "bodoh", "tolol", "goblok", "idiot", "kampret", "bajingan", "asu", "pantat", "anjrit", "bjir", "hali", "halik", "cukimai",
            "jancok", "fuck", "shit", "bitch", "asshole", "perek", "lonte","jelek",
            "sialan", "setan", "iblis", "pepek", "titit", "itil", "peli",
        )

        fun validasiKomentar(teks: String): Pair<Boolean, String> {
            val trimmed = teks.trim()
            if (trimmed.length < 3) {
                return Pair(false, "Komentar terlalu pendek (minimal 3 karakter)!")
            }
            if (trimmed.length > 250) {
                return Pair(false, "Komentar terlalu panjang (maksimal 250 karakter)!")
            }

            // Normalisasi teks untuk deteksi bypass (misal: "a.n.j.i.n.g", "b@ngs@t")
            val normalized = trimmed.lowercase()
                .replace(Regex("[^a-zA-Z0-9]"), "")
                .replace("0", "o")
                .replace("1", "i")
                .replace("3", "e")
                .replace("4", "a")
                .replace("@", "a")

            for (kata in kataKasarList) {
                if (normalized.contains(kata) || trimmed.lowercase().split(Regex("\\s+")).contains(kata)) {
                    return Pair(false, "⚠️ Komentar mengandung kata tidak pantas! Harap gunakan bahasa yang sopan.")
                }
            }

            // Cek apakah komentar hanya berisi karakter berulang (spam)
            if (Regex("^(.)\\1{5,}$").matches(trimmed)) {
                return Pair(false, "Komentar terdeteksi sebagai spam berulang!")
            }

            return Pair(true, "")
        }

        fun sendComment() {
            val text = etComment?.text?.toString()?.trim().orEmpty()
            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "Komentar tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return
            }

            val (isValid, errorMessage) = validasiKomentar(text)
            if (!isValid) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                return
            }

            // Tambahkan langsung ke tampilan (optimistic UI) dengan nama pengguna yang ramah (bukan UUID mentah)
            val avatarColor = avatarColors[adapter.itemCount % avatarColors.size]
            val currentUsername = context?.getSharedPreferences("BakerysPrefs", android.content.Context.MODE_PRIVATE)
                ?.getString("profile_name", null)
                ?: try { SupabaseClient.instance.auth.currentUserOrNull()?.email?.substringBefore("@") } catch (e: Exception) { null }
                ?: "Baker"
            val newComment = CommentItem(currentUsername, text, avatarColor)
            adapter.addComment(newComment)

            etComment?.text?.clear()
            rvComments?.smoothScrollToPosition(adapter.itemCount - 1)

            // Simpan ke Supabase secara asinkron menggunakan fungsi BakeryStorageManager.simpanKomentarKeSupabase
            val targetResepId = if (resepId > 0L) resepId else null
            BakeryStorageManager.simpanKomentarKeSupabase(
                resepId = targetResepId,
                isiKomentar = text,
                context = requireContext()
            ) { berhasil, errorMsg ->
                activity?.runOnUiThread {
                    if (berhasil) {
                        Toast.makeText(requireContext(), "Komentar berhasil dikirim!", Toast.LENGTH_SHORT).show()
                        lifecycleScope.launch {
                            val validId = BakeryStorageManager.dapatkanResepIdValid(targetResepId)
                            val updated = BakeryStorageManager.muatKomentarDariSupabase(validId)
                            if (updated.isNotEmpty()) {
                                commentsList.clear()
                                commentsList.addAll(updated)
                                adapter.notifyDataSetChanged()
                                rvComments?.smoothScrollToPosition(commentsList.size - 1)
                            }
                        }
                    } else {
                        val pesan = errorMsg ?: "Kesalahan koneksi database"
                        Toast.makeText(requireContext(), "Gagal: $pesan", Toast.LENGTH_LONG).show()
                        android.util.Log.e("KOMENTAR_SHEET", "Gagal kirim komentar: $pesan")
                    }
                }
            }
        }

        btnSend?.setOnClickListener {
            sendComment()
        }

        etComment?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendComment()
                true
            } else {
                false
            }
        }

        return view
    }
}