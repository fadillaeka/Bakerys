@file:OptIn(
    io.github.jan.supabase.annotations.SupabaseExperimental::class,
    kotlinx.serialization.InternalSerializationApi::class
)
package com.example.bakerys

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

// 1. Data Model

// 2. Adapter
class ResepAdapter(private val listResep: List<ResepModel>) :
    RecyclerView.Adapter<ResepAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgKue: ImageView? = itemView.findViewById(R.id.imgKue)
        val btnFavorite: ImageView? = itemView.findViewById(R.id.btnFavorite)
        val tvJudulKue: TextView? = itemView.findViewById(R.id.tvJudulKue)
        val tvWaktuLevel: TextView? = itemView.findViewById(R.id.tvWaktuLevel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_resipe_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listResep[position]

        holder.tvJudulKue?.text = item.judul
        holder.tvWaktuLevel?.text = item.deskripsi

        // Pasang gambar kue secara dinamik
        holder.imgKue?.setImageResource(item.gambar ?: R.drawable.dubai)

        // Sinkronkan status favorit awal dari DisukaiFragment agar warna selalu konsisten saat di-scroll
        item.isFavorite = DisukaiFragment.daftarResepDisukai.any { it.judul == item.judul }
        updateHeartColor(holder.btnFavorite, item.isFavorite)

        // --- AKSI KLIK TOMBOL FAVORIT / LIKE ---
        holder.btnFavorite?.setOnClickListener {
            item.isFavorite = !item.isFavorite

            if (item.isFavorite) {
                DisukaiFragment.tambahKeDisukai(item)
                Toast.makeText(holder.itemView.context, "${item.judul} ditambahkan ke Disukai", Toast.LENGTH_SHORT).show()
            } else {
                DisukaiFragment.daftarResepDisukai.removeAll { it.judul == item.judul }
                Toast.makeText(holder.itemView.context, "${item.judul} dihapus dari Disukai", Toast.LENGTH_SHORT).show()
            }

            // Update warna secara instan
            updateHeartColor(holder.btnFavorite, item.isFavorite)
        }

        // --- AKSI KLIK KARTU UNTUK MEMBUKA DETAIL RESEP ---
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailResepActivity::class.java).apply {
                putExtra("EXTRA_JUDUL", item.judul)
                putExtra("EXTRA_GAMBAR", item.gambar)

                // Format durasi dinamis agar tidak default ke 50 menit
                val durasiFormat = if (item.deskripsi.contains("•")) {
                    "⏱️ " + item.deskripsi.split("•").last().trim()
                } else {
                    "⏱️ " + item.deskripsi
                }
                putExtra("EXTRA_WAKTU", durasiFormat)
                putExtra("EXTRA_TENTANG", "Resep olahan lezat ${item.judul} yang mudah dan praktis dibuat di rumah.")
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listResep.size

    // Helper function untuk mengubah warna ikon secara langsung & aman
    private fun updateHeartColor(imageView: ImageView?, isFavorite: Boolean) {
        if (isFavorite) {
            imageView?.setColorFilter(Color.parseColor("#FF3B30")) // Merah menyala
        } else {
            imageView?.setColorFilter(Color.parseColor("#8C7A6B")) // Abu-abu / Cokelat tua
        }
    }
}