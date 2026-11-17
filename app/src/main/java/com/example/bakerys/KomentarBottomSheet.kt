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
    private val commentsList: MutableList<CommentItem>
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

        btnClose?.setOnClickListener {
            dismiss()
        }

        // Daftar kata-kata terlarang / tidak pantas (Bad Word Filter)
        val kataKasarList = listOf(
            "anjing", "babi", "bangsat", "kontol", "memek", "jembut", "pantek",
            "bodoh", "tolol", "goblok", "idiot", "kampret", "bajingan", "asu",
            "jancok", "fuck", "shit", "bitch", "asshole", "perek", "lonte",
            "sialan", "setan", "iblis", "pepek", "titit", "itil", "peli"
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

            val nextProfileNumber = adapter.itemCount + 1
            val username = "Baker #$nextProfileNumber"
            val avatarColor = avatarColors[adapter.itemCount % avatarColors.size]

            val newComment = CommentItem(username, text, avatarColor)
            adapter.addComment(newComment)

            etComment?.text?.clear()
            rvComments?.smoothScrollToPosition(adapter.itemCount - 1)
            Toast.makeText(requireContext(), "Komentar berhasil dikirim!", Toast.LENGTH_SHORT).show()
        }

        fun kirimKomentar(resepId: Long, teksKomentar: String) {
            lifecycleScope.launch {
                try {
                    val currentUserId = SupabaseClient.instance.auth.currentUserOrNull()?.id ?: return@launch

                    val dataKomentar = buildJsonObject {
                        put("user_id", currentUserId)
                        put("resep_id", resepId)
                        put("isi_komentar", teksKomentar)
                    }

                    SupabaseClient.instance.from("komentar").insert(dataKomentar)
                    Toast.makeText(requireContext(), "Komentar terkirim!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Gagal kirim komentar: ${e.message}", Toast.LENGTH_LONG).show()
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