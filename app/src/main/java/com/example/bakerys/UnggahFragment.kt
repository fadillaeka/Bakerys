package com.example.bakerys

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class UnggahFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_unggah, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inisialisasi ID sesuai XML
        val etJudul = view.findViewById<EditText>(R.id.etJudul)
        val etDeskripsi = view.findViewById<EditText>(R.id.etDeskripsi)
        val btnUnggah = view.findViewById<Button>(R.id.btnPublikasikan)
            ?: view.findViewById<View>(R.id.btnSimpan)

        // 2. Event Klik Tombol Publikasikan
        btnUnggah?.setOnClickListener {
            val judulText = etJudul?.text?.toString()?.trim() ?: ""
            val deskripsiText = etDeskripsi?.text?.toString()?.trim() ?: ""

            if (judulText.isEmpty()) {
                Toast.makeText(requireContext(), "Judul resep tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            unggahResepKeSupabase(judulText, deskripsiText, etJudul, etDeskripsi)
        }
    }

    private fun unggahResepKeSupabase(
        judul: String,
        deskripsi: String,
        etJudul: EditText?,
        etDeskripsi: EditText?
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Ambil ID User dari Auth / Session
                val currentUser = SupabaseClient.instance.auth.currentUserOrNull()
                val currentSession = SupabaseClient.instance.auth.currentSessionOrNull()
                val userId = currentUser?.id ?: currentSession?.user?.id

                Log.d("UNGGAH_DEBUG", "User ID saat unggah: $userId")

                if (userId.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Silakan login terlebih dahulu!", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Buat objek JSON data resep
                val resepData = buildJsonObject {
                    put("user_id", userId)
                    put("judul", judul)
                    put("deskripsi", deskripsi)
                }

                // Insert ke tabel "resep" di Supabase
                SupabaseClient.instance.from("resep").insert(resepData)

                Toast.makeText(requireContext(), "Resep berhasil diunggah!", Toast.LENGTH_SHORT).show()

                // Bersihkan form setelah sukses
                etJudul?.setText("")
                etDeskripsi?.setText("")

            } catch (e: Exception) {
                Log.e("UNGGAH_DEBUG", "Error Unggah: ${e.message}", e)
                Toast.makeText(requireContext(), "Gagal mengunggah: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}